package com.hackathon.filesync;
import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import gnu.trove.TIntProcedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;


class MyFirstProgram {
 
	private static final Logger log = LoggerFactory.getLogger(MyFirstProgram.class);

  public static void main(String[] args) { 
   
	UnitTest1();
	UnitTest2();
	UnitTest3();
	UnitTest4();
	UnitTest5();
       
}

  static void UnitTest1()
  {//raju-saood-manish
	    ProximityManager proximityManager = ProximityManager.getInstance();
	    User user1 =  CreateUser(5, 4, 0, "raju");
	    User user2 =  CreateUser(7, 15, 0, "manish");
	    User user3 =  CreateUser(11, 11, 0, "saood");
	    proximityManager.addUser(user1);
	    proximityManager.addUser(user2);
	    proximityManager.addUser(user3);
	    User dest = user3;
	    User src = user1;
	    User user = proximityManager.getNearestUserToDest("7", src, dest);
	
	    System.out.println("The source user: " + src.getUserId() + "   Destination user: " + dest.getUserId() + " Direct distance:  " +  
	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), src.getClient().getLocation().getLattitude(), src.getClient().getLocation().getLongitude())
	    + " \nProximity user calculated by algorithm: " + user.getUserId() + "  Actual geo distance: "  +  
	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), user.getClient().getLocation().getLattitude(), user.getClient().getLocation().getLongitude()));


  }
  

  static void UnitTest2()
  {//manish-raju-saood
	    ProximityManager proximityManager = ProximityManager.getInstance();
	    //proximityManager.clear();
	    User user1 =  CreateUser(5, 4, 0, "raju");
	    User user2 =  CreateUser(7, 3, 0, "manish");
	    User user3 =  CreateUser(11, 11, 0, "saood");
	    proximityManager.addUser(user1);
	    proximityManager.addUser(user2);
	    proximityManager.addUser(user3);
	    User dest = user3;
	    User src = user1;
	    User user = proximityManager.getNearestUserToDest("7", src, dest);
	    System.out.println("The source user: " + src.getUserId() + "   Destination user: " + dest.getUserId() + " Direct distance:  " +  
	    	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), src.getClient().getLocation().getLattitude(), src.getClient().getLocation().getLongitude())
	    	    + " \nProximity user calculated by algorithm: " + user.getUserId() + "  Actual geo distance: "  +  
	    	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), user.getClient().getLocation().getLattitude(), user.getClient().getLocation().getLongitude()));
  }
  
  static void UnitTest3()
  {//raju-manish-saood
	    ProximityManager proximityManager = ProximityManager.getInstance();
	   // proximityManager.clear();
	    User user1 =  CreateUser(5, 4, 0, "raju");
	    User user2 =  CreateUser(7, 5, 0, "manish");
	    User user3 =  CreateUser(11, 11 , 0, "saood");
	    proximityManager.addUser(user1);
	    proximityManager.addUser(user2);
	    proximityManager.addUser(user3);
	    User dest = user3;
	    User src = user1;
	    User user = proximityManager.getNearestUserToDest("7", src, dest);
	    System.out.println("The source user: " + src.getUserId() + "   Destination user: " + dest.getUserId() + " Direct distance:  " +  
	    	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), src.getClient().getLocation().getLattitude(), src.getClient().getLocation().getLongitude())
	    	    + " \nProximity user calculated by algorithm: " + user.getUserId() + "  Actual geo distance: "  +  
	    	    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), user.getClient().getLocation().getLattitude(), user.getClient().getLocation().getLongitude()));
  }
  
  static void UnitTest4()
  {//raju-saood-manish
     ProximityManager proximityManager = ProximityManager.getInstance();
     User user1 =  CreateUser(5, 4, 0, "raju");
     User user2 =  CreateUser(7, 15, 0, "manish");
     User user3 =  CreateUser(11, 11, 0, "saood");
     User user4 =  CreateUser(10, 11, 0, "khan");
     
     proximityManager.addUser(user1);
     proximityManager.addUser(user2);
     proximityManager.addUser(user3);
     proximityManager.addUser(user4);
     
     User dest = user4;
     User src = user1;
     
     User user = proximityManager.getNearestUserToDest("7", src, dest);
     System.out.println("The source user: " + src.getUserId() + "   Destination user: " + dest.getUserId() + " Direct distance:  " +  
    		    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), src.getClient().getLocation().getLattitude(), src.getClient().getLocation().getLongitude())
    		    + " \nProximity user calculated by algorithm: " + user.getUserId() + "  Actual geo distance: "  +  
    		    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), user.getClient().getLocation().getLattitude(), user.getClient().getLocation().getLongitude()));
  }
  
  
  static void UnitTest5()
  {//raju-saood-manish
     ProximityManager proximityManager = ProximityManager.getInstance();
     User user1 =  CreateUser(7, 6, 0, "raju");
     User user2 =  CreateUser(7, 8, 0, "manish");
     User user3 =  CreateUser(7, 0, 8, "saood");
     User user4 =  CreateUser(7, 0, 6, "khan");
     User user5 =  CreateUser(7, 5, 5, "pavan");
     User user6 =  CreateUser(10, 7, 7, "ranjan");
     
     
     proximityManager.addUser(user1);
     proximityManager.addUser(user2);
     proximityManager.addUser(user3);
     proximityManager.addUser(user4);
     proximityManager.addUser(user5);
     proximityManager.addUser(user6);
     
     User dest = user6;
     User src = user1;
     
     User user = proximityManager.getNearestUserToDest("7", src, dest);
     System.out.println("The source user: " + src.getUserId() + "   Destination user: " + dest.getUserId() + " Direct distance:  " +  
    		    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), src.getClient().getLocation().getLattitude(), src.getClient().getLocation().getLongitude())
    		    + " \nProximity user calculated by algorithm: " + user.getUserId() + "  Actual geo distance: "  +  
    		    Utility.distance(dest.getClient().getLocation().getLattitude(), dest.getClient().getLocation().getLongitude(), user.getClient().getLocation().getLattitude(), user.getClient().getLocation().getLongitude()));
  }
  
/**
 * 
 */
public static User CreateUser(int checksum, float x, float y, String userId) {
	List<UserFileMetaData> list = new ArrayList<UserFileMetaData>();
      
    User user = Utility.createUser(Utility.createClient(Utility.createGeoLocation(x, y,"", ""), "20", 4444), userId, list);
    for(int i = checksum; i < checksum + 3; i++)
    {
    	user.addFileMetaData(Utility.createUserFileMetaData(Integer.toString(i), 1, "test" + Integer.toString(i)));
    }
  
    return user;
}
}
