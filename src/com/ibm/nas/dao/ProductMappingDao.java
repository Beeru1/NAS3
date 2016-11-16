package com.ibm.nas.dao;

/**
 * @author Parnika Sharma 
 */

import java.util.ArrayList;

import com.ibm.nas.dto.CircleDTO;
import com.ibm.nas.dto.ProductDTO;
import com.ibm.nas.dto.StateDTO;
import com.ibm.nas.exception.DAOException;

public interface ProductMappingDao {

	public  ArrayList<ProductDTO> getProductList() throws DAOException;
	public  ArrayList<CircleDTO> getCircleList() throws DAOException;
	public  ArrayList<StateDTO> getStateList() throws DAOException;
	public  ArrayList<ProductDTO> getProductLobList() throws DAOException;
	public  ArrayList<ProductDTO> getProductList(int productLobId) throws DAOException;
	public  ArrayList<ProductDTO> getProductListNewProductLob(String newProductLobName, String userLoginId) throws DAOException;
	public  ArrayList<ProductDTO> getProductListNewProductName(String newProductName,int productLobId,String userLoginId) throws DAOException;
	public int insertProductSynonym(ProductDTO productDto) throws DAOException;
	public  ArrayList<ProductDTO> getSynonymListBasedOnProduct(int selectedProductId) throws DAOException;
	public  String getDataForPinCode(String pinCode,int circleMstrId,int productlobId ) throws DAOException;
	public  String populateZoneCityCityZoneBasedOnCircle(int circleMstrId ) throws DAOException;
	

}
