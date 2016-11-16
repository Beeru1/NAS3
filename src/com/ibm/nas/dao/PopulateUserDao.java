package com.ibm.nas.dao;

import java.util.List;

import com.ibm.nas.dto.Login;
import com.ibm.nas.exception.DAOException;

public interface PopulateUserDao {
	/** 
	
	* Select user details from ARC_MODULE_MSTR,ARC_USER_MSTR,ARC_ACTOR_MSTR,ARC_CIRCLE_MSTR table.
	* @param dto  DTO Object holding the data.
	* @return  List 
	* @throws DAOException In case of an error
 **/

public List populateValues(Login dto) throws DAOException;

/** 

	* Select category details from ARC_CATEGORY_MSTR table.
	* @return  HashMap 
	* @throws DAOException In case of an error
**/
public List populateValuesforUD(String loginID) throws DAOException;

/** 

	* Select category details from ARC_CATEGORY_MSTR table.
	* @return  HashMap 
	* @throws DAOException In case of an error
**/
//public HashMap category() throws DAOException;
}
