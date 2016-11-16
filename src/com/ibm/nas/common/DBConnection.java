package com.ibm.nas.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ibm.nas.exception.DAOException;

public class DBConnection {
	
	private static Logger logger = Logger.getLogger(DBConnection.class);
	private static DataSource mem_o_datasource;
	private static DataSource hrms_datasource;
	private static int counter=0;
	
	/**
	 * The default private constructor. 
	 */
	public DBConnection() {
	}
	/**
	 * This method does the lookup of the application datasource and store it in a memeber variable to be 
	 * used later, to avoid doing lookups again and again
	 * @exception DAOException - This exception is thrown in case data source cannot be looked up
	 */
	private  static void getDataSource() throws DAOException {
		try {
			synchronized (DBConnection.class){
				if (mem_o_datasource == null) {	
					Context initContext = new InitialContext();
		        	Context loc_o_ic = (Context) initContext.lookup("java:comp/env");
					mem_o_datasource =
						(DataSource) loc_o_ic.lookup(
							PropertyReader.getValue("DATASOURCE_LOOKUP_NAME"));	
				}
			}
		} catch (NamingException namingException) {
			logger.error("Lookup of Data Source Failed. Reason:" + namingException.getMessage());
			throw new DAOException("Exception Occured while data source lookup.",namingException);
		}
	}
	
	/**
	 * This method does the lookup of the HRMS datasource and store it in a memeber variable to be 
	 * used later, to avoid doing lookups again and again.
	 * @exception DAOException - This exception is thrown in case data source cannot be looked up
	 */
	private  static void getHRMSDataSource() throws DAOException {
		try {
			synchronized (DBConnection.class){
				if (hrms_datasource == null) {
					InitialContext loc_o_ic = new InitialContext();
					hrms_datasource =(DataSource) loc_o_ic.lookup(PropertyReader.getValue("OLMSDS_LOOKUP_NAME"));					
				}
			}
		} catch (NamingException namingException) {
			logger.error("Lookup of LDAP Data Source Failed. Reason:" + namingException.getMessage());
			throw new DAOException("Exception Occured while data source lookup.",namingException);
		}
	}
	
	/**
	 * This method returns the application DB connection using datasource.
	 * @return Connection - The DB connection instance
	 * @exception DAOException - This exception is thrown in case connection is not established
	 */
	public static  Connection getDBConnection() throws DAOException {
		Connection dbConnection = null;
		try {
			counter++;
			if (mem_o_datasource == null) {
					getDataSource();
				}
				dbConnection =
				mem_o_datasource.getConnection();
		} catch (SQLException sqlException) {
			logger.error("Could Not Obtain Connection. Reason:" + sqlException.getMessage() + ". Error Code:" + sqlException.getErrorCode());
			throw new DAOException("Exception Occured while obtaining Connection.",sqlException);
		}
		return dbConnection;
	}
	
	/*public static  Connection getDBConnection() throws DAOException {
		Connection dbConnection = null;
		BasicDBObject bludb = null;
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		System.out.println("VCAP_SERVICES content******************: " + VCAP_SERVICES);
		try {
			if (VCAP_SERVICES != null) {
				// parse the VCAP JSON structure
				BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
				String thekey = null;
				Set<String> keys = obj.keySet();
				System.out.println("Searching through VCAP keys");
				// Look for the VCAP key that holds the dashDB information
				for (String eachkey : keys) {
					System.out.println("Key is: " + eachkey);
					// The old name for this service was AnalyticsWarehouse
					if (eachkey.contains("dashDB")) {
						thekey = eachkey;
					}
				}
				if (thekey == null) {
					System.out.println("Cannot find any dashDB service in the VCAP; exiting");
					return null;
				}

				BasicDBList list = (BasicDBList) obj.get(thekey);
				bludb = (BasicDBObject) list.get("0");
				System.out.println("Service found: " + bludb.get("name"));
				bludb = (BasicDBObject) bludb.get("credentials");
				
			} else {
				System.out.println("VCAP_SERVICES is null");
				return null;
			}
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// Connect to the database
			System.out.println("Connecting to the database");
			System.out.println();
			
			String jdbcurl =  (String) bludb.get("jdbcurl");
			String user = (String) bludb.get("username");
		    String password = (String) bludb.get("password");
			
		    dbConnection = DriverManager.getConnection(jdbcurl, user,password);
			// or if the username & password are in the url:
		    System.out.println("*******dbConnection*****=="+dbConnection);
			// con = DriverManager.getConnection(databaseUrl);
			// Commit changes manually
		    dbConnection.setAutoCommit(false);
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			//logger.error("Could Not Obtain Connection. Reason:" + sqlException.getMessage() + ". Error Code:" + sqlException.getErrorCode());
			throw new DAOException("Exception Occured while obtaining Connection.",sqlException);
		}
		return dbConnection;
	}*/
	
	/**
	 * This method returns the HRMS DB connection using datasource.
	 * @return Connection - The HRMS database connection instance.
	 * @exception DAOException - This exception is thrown in case connection is not established.
	 */
	public static  Connection getOLMSConnection() throws DAOException {
		Connection oracleConnection = null;
		try {
			
			if (hrms_datasource == null) {
				getHRMSDataSource();
				}
			oracleConnection =
				hrms_datasource.getConnection();
			logger.info("Connection Obtained.");
		} catch (SQLException sqlException) {
			logger.error("Couldn't connected to HRMS server. Reason:" + sqlException.getMessage() + ". Error Code:" + sqlException.getErrorCode());
			throw new DAOException("Couldn't connected to HRMS server.");
		}
		return oracleConnection;
	}
	
	public static void releaseResources(
		Connection connection,
		Statement statement,
		ResultSet resultSet)
		throws DAOException {
		try {
						counter--;
			if (resultSet != null) {
				resultSet.close();
				resultSet = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException sqlException) {
			logger.error("Closing of Resources Failed. Reason:" + sqlException.getMessage()+". Error Code:"+ sqlException.getErrorCode());
			throw new DAOException("errors.dbconnection.close_connection");
		}
	}
}
