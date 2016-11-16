package com.ibm.nas.dao.impl;

/**
 * @author Parnika Sharma 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ibm.nas.dao.ProductMappingDao;
import com.ibm.nas.common.DBConnection;
import com.ibm.nas.dto.CircleDTO;
import com.ibm.nas.dto.ProductDTO;
import com.ibm.nas.dto.StateDTO;
import com.ibm.nas.exception.DAOException;


public class ProductMappingDaoImpl implements ProductMappingDao {

	Logger logger = Logger.getLogger(ProductMappingDaoImpl.class);
	
private static ProductMappingDaoImpl productMappingDaoImpl=null;
	
	private ProductMappingDaoImpl(){
		
	}
	
	public static ProductMappingDaoImpl productMappingDaoInstance()
	{
		if(productMappingDaoImpl==null)
		{
			productMappingDaoImpl=new ProductMappingDaoImpl();
		}
		return productMappingDaoImpl;
		
	}
	
	public  ArrayList<ProductDTO> getProductList() throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> productList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("select PRODUCT_ID, PRODUCT_LOB_ID, PRODUCT_NAME, PRODUCT_DESC, PRODUCT_HEAD_OLM_ID from PRODUCT   ");
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new ProductDTO();
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				dto.setProductDescription(rs.getString("PRODUCT_DESC"));
				dto.setProductHeadOlmId(rs.getString("PRODUCT_HEAD_OLM_ID"));
				productList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return productList;
	}
	
	public  ArrayList<ProductDTO> getProductLobList() throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> productList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("SELECT PRODUCT_LOB, PRODUCT_LOB_ID FROM PRODUCT_LOB where STATUS='A'    ");
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new ProductDTO();
				dto.setProductLobId(rs.getInt("PRODUCT_LOB_ID"));
				dto.setProductLobName(rs.getString("PRODUCT_LOB"));
				productList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return productList;
	}
	
	public  ArrayList<ProductDTO> getProductList(int productLobId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> productList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("SELECT PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESC, PRODUCT_HEAD_OLM_ID FROM PRODUCT_MSTR WHERE PRODUCT_LOB_ID = ? and STATUS='A'  ");
			ps.setInt(1, productLobId);
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new ProductDTO();
				dto.setProductId(rs.getInt("PRODUCT_ID"));
				dto.setProductName(rs.getString("PRODUCT_NAME"));
				//dto.setProductDescription(rs.getString("PRODUCT_DESC"));
				//dto.setProductHeadOlmId(rs.getString("PRODUCT_HEAD_OLM_ID"));
				productList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list for the given product LOB :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return productList;
	}
	
	public  ArrayList<StateDTO> getStateList() throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<StateDTO> stateList = new ArrayList<StateDTO>();
		StateDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("select  STATE_ID,     STATE_NAME,     STATE_DESC  from STATE_MSTR  ");
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new StateDTO();
				dto.setStateId(rs.getInt("STATE_ID"));
				dto.setStateName(rs.getString("STATE_NAME"));
				dto.setStateDescription(rs.getString("STATE_DESC"));
				stateList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return stateList;
	}
	
	public  ArrayList<CircleDTO> getCircleList() throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<CircleDTO> circleList = new ArrayList<CircleDTO>();
		CircleDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("select   CIRCLE_ID  ,   CIRCLE_NAME  ,   CIRCLE_DESC  from CIRCLE_MSTR  ");
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new CircleDTO();
				dto.setCircleId(rs.getInt("CIRCLE_ID"));
				dto.setCircleName(rs.getString("CIRCLE_NAME"));
				dto.setCircleDescription(rs.getString("CIRCLE_DESC"));
				circleList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting product list :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return circleList;
	}
	
	public  ArrayList<ProductDTO> getProductListNewProductLob(String newProductLobName, String userLoginId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> newproductList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("INSERT INTO PRODUCT_LOB(PRODUCT_LOB, STATUS, TRANSACTION_TIME, UPDATED_BY) VALUES(?, 'A', current timestamp, ?)");
			ps.setString(1, newProductLobName);
			ps.setString(2, userLoginId);
			ps.executeUpdate();
			newproductList = getProductLobList();
		} catch (Exception e) {
			throw new DAOException("Exception occured while getProductListNewProductLob() :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return newproductList;
	}
	
	public  ArrayList<ProductDTO> getProductListNewProductName(String newProductName,int productLobId, String userLoginId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> newproductList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("INSERT INTO PRODUCT_MSTR(PRODUCT_LOB_ID,PRODUCT_NAME, PRODUCT_DESC, STATUS, TRANSACTION_TIME, UPDATED_BY) VALUES(?,?, ?, 'A', current timestamp, ?)");
			ps.setInt(1, productLobId);
			ps.setString(2, newProductName);	
			ps.setString(3, newProductName);
			ps.setString(4, userLoginId);	
			ps.executeUpdate();
			newproductList = getProductList(productLobId);
		} catch (Exception e) {
			throw new DAOException("Exception occured while getProductListNewProductName() :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return newproductList;
	}
	
	public int insertProductSynonym(ProductDTO productDto) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int rows =0;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("INSERT INTO PRODUCT_SYNONYM(PRODUCT_SYNONYM_NAME,PRODUCT_ID,STATUS, TRANSACTION_TIME, UPDATED_BY) VALUES(?,?,'A', current timestamp, ?)  ");			
			ps.setString(1, productDto.getNewProductSynonym());
			ps.setInt(2, productDto.getProductId());
			ps.setString(3, productDto.getUpdatedBy());
			rows = ps.executeUpdate();
		} catch (Exception e) {
			throw new DAOException("Exception occured while insertProductSynonym() :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return rows;
	}
	
	public  ArrayList<ProductDTO> getSynonymListBasedOnProduct(int selectedProductId) throws DAOException
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ProductDTO> synonymList = new ArrayList<ProductDTO>();
		ProductDTO dto = null;
		try {
			con = DBConnection.getDBConnection();
			ps = con.prepareStatement("select PRODUCT_SYNONYM_NAME,PRODUCT_SYNONYM_ID from PRODUCT_SYNONYM where PRODUCT_ID = ? and STATUS = 'A'  ");
			ps.setInt(1, selectedProductId);
			rs = ps.executeQuery();
			while(rs.next()) {
				dto = new ProductDTO();
				dto.setSynonymName(rs.getString("PRODUCT_SYNONYM_NAME"));
				dto.setSynonymId(rs.getInt("PRODUCT_SYNONYM_ID"));
				synonymList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException("Exception occured while getting getSynonymListBasedOnProduct() :  "+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return synonymList;
	}
	// adding by pratap 
	public String getDataForPinCode(String pinCode,int circleMstrId ,int productlobId) throws DAOException {
		System.out.println(" in getDataForPinCode of productmappingdaoimpl :::::");
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String cityCode="";
		String cityName="";
		String zone_code="";
		String zone_name="";
		String cityZone_code="";
		String cityZone_name="";
		int circleMstr_Id=-1;
		String GET_ALL_DETAILS_ON_PINCODE =null;
		if(circleMstrId !=0 && productlobId==0) {
			 GET_ALL_DETAILS_ON_PINCODE="SELECT pm.CITY_ZONE_CODE,czm.CITY_ZONE_NAME, czm.CITY_CODE,ctm.CITY_NAME,ctm.ZONE_CODE,zm.ZONE_NAME,zm.CIRCLE_MSTR_ID FROM CIRCLE_MSTR CM,ZONE_MSTR ZM,CITY_MSTR CTM ,CITY_ZONE_MSTR CZM,PINCODE_MSTR PM "+
			 "WHERE  PM.CITY_ZONE_CODE=czm.CITY_ZONE_CODE AND czm.CITY_CODE=ctm.CITY_CODE AND ctm.ZONE_CODE= zm.ZONE_CODE AND "+
			"zm.CIRCLE_MSTR_ID= cm.CIRCLE_MSTR_ID and pm.PINCODE=? AND cm.CIRCLE_MSTR_ID=? AND pm.STATUS='A'  ";
		}else {
			 GET_ALL_DETAILS_ON_PINCODE="SELECT pm.CITY_ZONE_CODE,czm.CITY_ZONE_NAME, czm.CITY_CODE,ctm.CITY_NAME,ctm.ZONE_CODE,zm.ZONE_NAME,zm.CIRCLE_MSTR_ID FROM CIRCLE_MSTR CM,ZONE_MSTR ZM,CITY_MSTR CTM ,CITY_ZONE_MSTR CZM,PINCODE_MSTR PM "+
			 "WHERE  PM.CITY_ZONE_CODE=czm.CITY_ZONE_CODE AND czm.CITY_CODE=ctm.CITY_CODE AND ctm.ZONE_CODE= zm.ZONE_CODE AND "+
			"zm.CIRCLE_MSTR_ID= cm.CIRCLE_MSTR_ID and pm.PINCODE=? AND cm.LOB_ID=? AND pm.STATUS='A'  ";
		}
		
		try {
			System.out.println("GET_ALL_DETAILS_ON_PINCODE :"+GET_ALL_DETAILS_ON_PINCODE+"  circleMstrId:"+circleMstrId+" pinCode:"+pinCode);
			 if (!pinCode.equalsIgnoreCase(""))
	    		{
				  con = DBConnection.getDBConnection();
					ps = con.prepareStatement(GET_ALL_DETAILS_ON_PINCODE);
					ps.setString(1, pinCode);
					if(circleMstrId !=0 && productlobId==0) {
					ps.setInt(2, circleMstrId);
					}else {
						ps.setInt(2, productlobId);	
					}
				  	rs=ps.executeQuery();
	    			if(rs.next())
	    			{ 
	    				cityZone_code=rs.getString("CITY_ZONE_CODE");
	    				cityZone_name=rs.getString("CITY_ZONE_NAME");
	    				
	    				cityCode=rs.getString("CITY_CODE");
	    				cityName=rs.getString("CITY_NAME");
	    				
	    				zone_code=rs.getString("ZONE_CODE");
	    				zone_name=rs.getString("ZONE_NAME");
	    				
	    				circleMstr_Id=rs.getInt("CIRCLE_MSTR_ID");
	    			}
	    			else
	    			{
	    			return "";
	    			}
	    			 System.out.println("circleMstrId != circleMstr_Id :"+" "+(circleMstrId != circleMstr_Id));
	    			 System.out.println("cityZone_code :"+cityZone_code+"  cityzone_name :"+cityZone_name+" citycode :"+cityCode+" cityname :"+cityName+"  zonecode  :"+zone_code+" zone_name : "+zone_name);
	    		}
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting gData for pincode : "+pinCode+" :Exception"+ e.getMessage(),e);
		} finally {
			try {
				///DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return cityZone_code+"#"+cityZone_name+"#"+cityCode+"#"+cityName+"#"+zone_code+"#"+zone_name;
	}

	// end of adding by pratap
	
	public String populateZoneCityCityZoneBasedOnCircle(int circleMstrId) throws DAOException {
		System.out.println(" in getDataForPinCode of productmappingdaoimpl :::::");
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String cityCode="";
		String cityName="";
		String zone_code="";
		String zone_name="";
		String cityZone_code="";
		String cityZone_name="";
		int circleMstr_Id=-1;
		StringBuffer TotalVal=new StringBuffer("");
		/*String GET_ALL_DETAILS_FOR_CIRCLEMASTERID=" SELECT distinct pm.CITY_ZONE_CODE,czm.CITY_ZONE_NAME, czm.CITY_CODE,ctm.CITY_NAME,ctm.ZONE_CODE,zm.ZONE_NAME,zm.CIRCLE_MSTR_ID "+
 " FROM CIRCLE_MSTR CM,ZONE_MSTR ZM,CITY_MSTR CTM ,CITY_ZONE_MSTR CZM,PINCODE_MSTR PM WHERE   CM.CIRCLE_MSTR_ID=? AND CM.STATUS='A' AND CM.CIRCLE_MSTR_ID=ZM.CIRCLE_MSTR_ID AND ZM.STATUS='A' "+
" AND ZM.ZONE_CODE=CTM.ZONE_CODE AND CTM.STATUS='A' AND CTM.CITY_CODE=CZM.CITY_CODE AND CZM.STATUS='A'";
		*/
		String GET_ALL_DETAILS_FOR_CIRCLEMASTERID="SELECT CZM.CITY_ZONE_CODE,CZM.CITY_ZONE_NAME, CTM.CITY_CODE,CTM.CITY_NAME,ZONE.ZONE_CODE,ZONE.ZONE_NAME " +
												  " FROM CITY_MSTR CTM,(SELECT ZONE_CODE,ZONE_NAME FROM ZONE_MSTR WHERE CIRCLE_MSTR_ID=?) AS ZONE,CITY_ZONE_MSTR CZM "+
												 " WHERE CTM.ZONE_CODE= ZONE.ZONE_CODE AND CZM.CITY_CODE=CTM.CITY_CODE";
		try {
			System.out.println("GET_ALL_DETAILS_FOR_CIRCLEMASTERID :"+GET_ALL_DETAILS_FOR_CIRCLEMASTERID+"  circleMstrId:"+circleMstrId);
			
				   con = DBConnection.getDBConnection();
					ps = con.prepareStatement(GET_ALL_DETAILS_FOR_CIRCLEMASTERID);
					ps.setInt(1, circleMstrId);
				  	rs=ps.executeQuery();
	    			while(rs.next())
	    			{ 
	    				cityZone_code=rs.getString("CITY_ZONE_CODE");
	    				cityZone_name=rs.getString("CITY_ZONE_NAME");
	    				
	    				cityCode=rs.getString("CITY_CODE");
	    				cityName=rs.getString("CITY_NAME");
	    				
	    				zone_code=rs.getString("ZONE_CODE");
	    				zone_name=rs.getString("ZONE_NAME");
	    				
	    				TotalVal=TotalVal.append(cityZone_code+"#"+cityZone_name+"#"+cityCode+"#"+cityName+"#"+zone_code+"#"+zone_name+"="); 
	    				
	    				//circleMstr_Id=rs.getInt("CIRCLE_MSTR_ID");
	    				
	    			}
	    			System.out.println("TotalVal :"+TotalVal);
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException("Exception occured while getting zone, city, cityzone for circle master id : "+circleMstrId+" :Exception"+ e.getMessage(),e);
		} finally {
			try {
				DBConnection.releaseResources(con, ps, rs);
			} catch (Exception e) {				
				throw new DAOException(e.getMessage(), e);
			}
		}
		return TotalVal.toString();
	}

}
