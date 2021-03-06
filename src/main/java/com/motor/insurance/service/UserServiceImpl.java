package com.motor.insurance.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motor.insurance.dao.UserDao;
import com.motor.insurance.entity.Proposal;
import com.motor.insurance.entity.User;
import com.motor.insurance.exception.ResourceNotFoundException;
import com.motor.insurance.model.UserModel;


@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	UserDao userDao;

	
	
	@Autowired
	EntityManager em;
	
	
	List<UserModel> userList = new ArrayList<UserModel>();

	@Override
	public void createUser(UserModel userModel) {
	//	 BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();;
		
			// TODO Auto-generated method stub
			User userEntity = new User();
			userEntity.setEmail(userModel.getUserEmail());
			userEntity.setName(userModel.getUserName());
			//userEntity.setPassword(userModel.getUserPassword());
			userEntity.setPassword(PasswordEncoder(userModel.getUserPassword()));

			userDao.save(userEntity);

		}

	@Override
	public List<UserModel> findUserById(int userId)   {
	     System.out.println("--------------------userID--------------"+userId);
		UserModel user = new UserModel();
		   Optional<User> userEntity = userDao.findById(userId);
		   if(userEntity.isPresent()) {
			  User userObj = userEntity.get();
			  user.setUserName(userObj.getName());
			  user.setUserEmail(userObj.getEmail());
			  user.setUserPassword(userObj.getPassword());
			   this.userList.add(user);
		   }
		
		   else {
				throw new ResourceNotFoundException("Record not found with userid:"+ userId);
			}		  
			   
		      return userList;
	}

	@Override
	public void updateProfile(UserModel userModel) {
       
		// TODO Auto-generated method stub
		
		
				Optional<User> UserDb = this.userDao.findById(userModel.getUserId());
				System.out.println("------usrmodel----" + userModel.getUserId());
				if (UserDb.isPresent()) {
					User userUpdate = UserDb.get();
					userUpdate.setName(userModel.getUserName());
					userUpdate.setEmail(userModel.getUserEmail());
					userUpdate.setPassword(PasswordEncoder(userModel.getUserPassword()));

					userDao.save(userUpdate);

				} else {
					throw new ResourceNotFoundException("Record not found with id : " + userModel.getUserId());
				
				}
		
		
	}

	
	/*
	 * for check if has same email in db , so that duplicate email is not allow
	 */	@Override
	public boolean searchByUserEmail(UserModel userModel) {
        boolean flag =false;

		CriteriaBuilder cb=em.getCriteriaBuilder();
		CriteriaQuery<User> cq=cb.createQuery(User.class);
		System.out.println("---------Search user Repo-------------"+userModel.getUserEmail());
		Root<User> proposal=cq.from(User.class);
			try {
			Predicate p = cb.equal(proposal.get("email"), userModel.getUserEmail().toLowerCase().trim());
			cq.where(cb.and(p)).distinct(true);

			TypedQuery<User> typedQuery = em.createQuery(cq);
			List<User> resultList = typedQuery.getResultList();

			if (resultList.isEmpty() || resultList.size() == 0) {
				flag = false;
			}

			else
				flag = true;

	        
	}
	catch (IllegalArgumentException e) {
		System.out.println("IllegalArgumentException");
		e.printStackTrace();
	}
			return flag;
	



	

	
	}

	@Override
	public List<UserModel> checkAuthentication(UserModel userModel) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		System.out.println("---------User Login -------------" + userModel.getUserEmail()+userModel.getUserPassword().trim());
		Root<User> user = cq.from(User.class);
		String password = PasswordEncoder(userModel.getUserPassword().trim());
		Predicate p = cb.equal(user.get("email"), userModel.getUserEmail().trim());
	    Predicate p1 = cb.equal(user.get("password"), password);

		cq.where(p,p1).distinct(true);
		TypedQuery<User> typedQuery = em.createQuery(cq);
		List<User> resultList = new ArrayList<User>();
         UserModel model = new UserModel();
         List<UserModel> modellist = new ArrayList<UserModel>();
		try {
	         System.out.println("model list"+resultList);

			resultList = typedQuery.getResultList();
			
			if (!resultList.isEmpty()) {
				System.out.println();
			/*	System.out.println("Match Password>>>>>"+bCryptPasswordEncoder.matches(userModel.getUserPassword(), resultList.get(0).getPassword()));
				if (bCryptPasswordEncoder.matches(userModel.getUserPassword(), resultList.get(0).getPassword())) {*/
					for (User u : resultList) {
						model.setUserEmail(u.getEmail() );
						model.setUserName(u.getName());
						model.setUserId(u.getId());
						model.setUserPassword(PasswordEncoder(u.getPassword()));
						System.out.println("-----ssssss----" + resultList);
					}
					modellist.add(model);
			//	}
				}
				
            
			
		} 
		catch (ArrayIndexOutOfBoundsException e) {
			 System.out.println("ArrayIndexOutOfBoundsException");			}
		
		catch (NoResultException nre) {
			System.out.println("ERRO");
		}
		return modellist;

	
	}
	
	
	
	
	String PasswordEncoder(String name) {
		String password = name;
		String algorithm = "SHA";

		byte[] plainText = password.getBytes();
	    StringBuilder sb = new StringBuilder();

		try {
		    MessageDigest md = MessageDigest.getInstance(algorithm);

		    md.reset();
		    md.update(plainText);
		    byte[] encodedPassword = md.digest();

		    for (int i = 0; i < encodedPassword.length; i++) {
		        if ((encodedPassword[i] & 0xff) < 0x10) {
		            sb.append("0");
		        }

		        sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
		    }

		    System.out.println("Plain    : " + password);
		    System.out.println("Encrypted: " + sb.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return sb.toString();
	}
}



