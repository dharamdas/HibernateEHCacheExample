package com.hibernate.main;

import com.hibernate.model.Address;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

import com.hibernate.model.Employee;
import com.hibernate.util.HibernateUtil;

public class HibernateEHCacheMain {

	public static void main(String[] args) {
		
		System.out.println("Temp Dir:"+System.getProperty("java.io.tmpdir"));
		insertData();
		//Initialize Sessions
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Statistics stats = sessionFactory.getStatistics();
		System.out.println("Stats enabled="+stats.isStatisticsEnabled());
		stats.setStatisticsEnabled(true);
		System.out.println("Stats enabled="+stats.isStatisticsEnabled());
		
		Session session = sessionFactory.openSession();
		Session otherSession = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Transaction otherTransaction = otherSession.beginTransaction();
		
		printStats(stats, 0);
		
		Employee emp = (Employee) session.load(Employee.class, 1L);
		printData(emp, stats, 1);
		
		emp = (Employee) session.load(Employee.class, 1L);
		printData(emp, stats, 2);
		
		//clear first level cache, so that second level cache is used
		session.evict(emp);
		System.out.println("Second Level Cache should hit");
		emp = (Employee) session.load(Employee.class, 1L);
		printData(emp, stats, 3);
		
		emp = (Employee) session.load(Employee.class, 3L);
		printData(emp, stats, 4);
		
		emp = (Employee) otherSession.load(Employee.class, 1L);
		printData(emp, stats, 5);
		
		//Release resources
		transaction.commit();
		otherTransaction.commit();
		sessionFactory.close();
	}

	private static void printStats(Statistics stats, int i) {
		System.out.println("***** " + i + " *****");
		System.out.println("Fetch Count="
				+ stats.getEntityFetchCount());
		System.out.println("Second Level Hit Count="
				+ stats.getSecondLevelCacheHitCount());
		System.out
				.println("Second Level Miss Count="
						+ stats
								.getSecondLevelCacheMissCount());
		System.out.println("Second Level Put Count="
				+ stats.getSecondLevelCachePutCount());
	}

	private static void printData(Employee emp, Statistics stats, int count) {
		System.out.println(count+":: Name="+emp.getName()+", Zipcode="+emp.getAddress().getZipcode());
		printStats(stats, count);
	}

	private static void insertData(){

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		System.out.println("Inserting Data into Hibernate");
		Employee emp = new Employee();
		emp.setName("Ravi");
		emp.setSalary(80000);

		Address address=new Address();
		emp.setAddress(address);
		address.setAddressLine1("Rammurhty Nagar");
		address.setCity("Bangalore");
		address.setZipcode("560045");
		address.setEmployee(emp);

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.save(emp);
		session.save(address);
		transaction.commit();
		session.close();
	}

}
