package org.reactor.monitoring.application.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.reactor.monitoring.application.Task;
import org.reactor.monitoring.common.ManagerType;
import org.reactor.monitoring.exception.ApplicationException;
import org.reactor.monitoring.model.AbstractDAOManager;
import org.reactor.monitoring.model.internal.DAOManagerFactory;
import org.reactor.monitoring.model.internal.MyDashboardDAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import com.hazelcast.nio.Address;


public class ApplicationClusterUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationClusterUtil.class);
	
	public static HazelcastInstance getClusterInstance(){
		Set<HazelcastInstance> nodes = Hazelcast.getAllHazelcastInstances();
		if(nodes == null || nodes.isEmpty()){
			LOGGER.warn("================================== Number of Local nodes ============================= "+0);
			return null;
		}
		
		LOGGER.info("================================== Number of Local nodes ============================= "+nodes.size());
		return nodes.iterator().next();
	}
	
	public static void loadBalanceTasks(Task task, final String mapName, final boolean reMap) throws ApplicationException{
		HazelcastInstance node = getClusterInstance();
		if(node == null){
			return;
		}
		
		MultiMap <String, String > distributedMap = node.getMultiMap( mapName );
			
		Cluster cluster = node.getCluster();
		Set<Member> members = cluster.getMembers();
		Member localMember = cluster.getLocalMember();
		Map<String,Member> mapByHost = new HashMap<>();
		for(Member member : members){
			mapByHost.put(member.getAddress().getHost(), member);
		}
		Member master = members.iterator().next();
		LOGGER.info("############################## Number of All nodes ################################# "+members.size());
		
		if(master.localMember() && reMap){
			LOGGER.info("================================== I am the Master ============================= ");			
			Map<String,Long> entityMap = MyDashboardDAOManager.getEntityMap();
			Set<String> ids = entityMap.keySet();
			if(ids == null || ids.isEmpty() ){
				LOGGER.warn("================================== Entity Map is empty or null ============================= ");
				return;
			}
			
			mapIdChunks(members, distributedMap, ids);	
			LOGGER.info("================================== Mastr node all product ids "+distributedMap+" ===========================");

		}else{
			LOGGER.info("================================== Mastr node is "+master.getAddress().getHost()+" ===========================");
		}
		
		String ip = localMember.getAddress().getHost();
		int port = localMember.getAddress().getPort();
		String key = ip+":"+port;
		Collection<String> productIds = distributedMap.get(key);
		LOGGER.info("############################ product Ids For "+key+" is "+productIds+"####################################");
	}
	
	public static void loadBalanceTasks(Task task, final MultiMap <String, String > distributedMap, final Set<String> ids, final boolean reMap) throws ApplicationException{
		HazelcastInstance node = getClusterInstance();
		if(node == null){
			return;
		}
					
		Cluster cluster = node.getCluster();
		Set<Member> members = cluster.getMembers();
		Member localMember = cluster.getLocalMember();
		Map<String,Member> mapByHost = new HashMap<>();
		for(Member member : members){
			mapByHost.put(member.getAddress().getHost(), member);
		}
		Member master = members.iterator().next();		
		LOGGER.info("############################## Number of All nodes ################################# "+members.size());
		
		if(master.localMember() && reMap){
			LOGGER.info("================================== I am the Master ============================= ");
			
			if(ids == null || ids.isEmpty() ){
				LOGGER.warn("================================== Entity Map is empty or null ============================= ");
				return;
			}
			mapIdChunks(members, distributedMap, ids);			
			LOGGER.info("================================== Mastr node all product ids "+distributedMap+" ===========================");

		}else{
			LOGGER.info("================================== Mastr node is "+master.getAddress().getHost()+" ===========================");
		}
		
		String ip = localMember.getAddress().getHost();
		int port = localMember.getAddress().getPort();
		String key = ip+":"+port;
		Collection<String> idCollection = distributedMap.get(key);
		LOGGER.info("############################ Ids For "+key+" is "+idCollection+"####################################");
	}
	
	private static void mapIdChunks(final Set<Member> members, final MultiMap <String, String > distributedMap, final Set<String> ids){
		int memberCount = members.size();
		List<List<String>> idChunks = chunk(new ArrayList(ids), memberCount);
		Iterator<Member>  hosts = members.iterator();
		for(List<String> chunk : idChunks){
			Address address = hosts.next().getAddress();
			String host = address.getHost();
			int port = address.getPort();
			String key = host+":"+port;
			distributedMap.remove(key);
			for(String id : chunk){
				distributedMap.put(key, id);
			}
			
		}	
	}
	
	public static void listParentEntities(final Map<String,String> config){
		AbstractDAOManager<Session> daoManager = (AbstractDAOManager<Session>)DAOManagerFactory.getDAOManager(ManagerType.DEFAULT);
		Session session;
		try {
			session = daoManager.getConnection(ManagerType.DEFAULT);
			MyDashboardDAOManager.list(session);
		} catch (Exception ex) {
			LOGGER.error(" Error while listing products "+ApplicationException.getStackTrace(ex));
		}
		
	}
		
	
	public static List<List<String>>  chunk(final List<String> arrayList, final int noOfBulks){

		int rest = arrayList.size() % noOfBulks;
		int max = arrayList.size() / noOfBulks;
		int start = 0;
		int end = 0;

		System.out.println("rest  " + rest + " max " + max);		
		List<List<String>> chunks = new ArrayList<>();
		for (int index = 0; index < noOfBulks; ++index) {
			if (index == 0) {
				end = start + max + rest;
			} else {
				end = start + max;
			}

			List<String> sublist = arrayList.subList(start, end);
			start = end;
			chunks.add(sublist);
			System.out.println(sublist);
		}		
		return chunks;		
		
	}

}
