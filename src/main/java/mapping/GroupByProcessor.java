package mapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.MappingException;
import exceptions.UnconsistantResourceException;
import mapping.nodes.DataTableCollection;
import mapping.nodes.GroupByCollection;
import mapping.types.Array;

import java.util.Set;

/**
 * 
 * Processor grouping table rows by some criteria and to transform the 
 * rootElement of mappingElements in a collection of rootElement
 *
 * @author laurentmichel
 *
 */
public class GroupByProcessor {
	/**
	 * Root Element of the hierarchy to be processed
	 * This is usually the root of a template mapping
	 */
	private GroupByCollection rootElement;
	/**
	 * dmrole of the column used to group data 
	 */
	private String groupByKey;

	/**
	 * @param rootElement The root Element of the hierarchy to be processed.
	 * It is supposed to a Collection on mode GROUP_BY. There isn checking at this level
	 * @throws Exception
	 */
	public GroupByProcessor(final GroupByCollection rootElement) throws Exception{
		this.rootElement = rootElement;
		this.groupByKey = this.rootElement.groupByKey;
	}

	/**
	 * Transform the initial hierachy in a {@link DataTableCollection} of hierarchies, 
	 * each one with a particular value of the grouped data.
	 * @return A map of hierarchies with the particular value of the grouped data as key
	 * @throws Exception
	 */
	public Map<String, MappingElement> run() throws Exception{
		/*
		 * Retrieve all collection which data must be grouped
		 * The map key is the role of these collections
		 */
		System.out.println("RUN Group by "  
				+ rootElement.getNodeId().role + " " + rootElement.getClass().getName() );

		Map<String, MappingElement>  tableCollections = this.getTableCollections();
		for( String key: tableCollections.keySet()){
			System.out.println("TABLECOLL " + key + " " 
					+ tableCollections.get(key).getNodeId().role + " " + tableCollections.get(key).getClass().getName());
		}
		/*
		 * Arrange the Collection content as a map of maps.
		 * The primary keys are the dmrole whereas the secondary keys are the key values
		 */
		Map<String, Map<String, Array>> groupedContents = this.getGroupedContent(tableCollections);
		for( Entry<String, Map<String, Array>> entry: groupedContents.entrySet() ) {
			System.out.println("GROUDATA " + entry.getKey() );
			for(String key: entry.getValue().keySet()){
				System.out.println("  " + key );
			}
		}
		/*
		 * Check data consistency
		 */
		this.checkGroupSizes(groupedContents);
		this.checkGroupKeys(groupedContents);
		/*
		 * remove the content of all colection which content is grouped
		 */
		this.removeTableCollectionData(tableCollections);
		/*
		 * Build the final map
		 */
		Map<String, MappingElement> rootCollection = new LinkedHashMap<>();
		MappingElement refRoot = this.rootElement;
		Set<String> keySet = this.getKeySet(groupedContents);
		if( keySet != null ){
			for( String key: keySet){
				System.out.println("adddd " + key);
				MappingElement root = deepClone(refRoot);
				System.out.println("adddd " + key);
				System.out.println("adddd " + root);
				/*
				 * Take the first child of the groupby  collection
				 */
				rootCollection.put(key, root);	
				this.setValueByKey(root, key, groupedContents);
			}
		}
		return rootCollection;
	}

	/**
	 * Return all the children of instance rootElement being a {@link DataTableCollection} in TABLE mode
	 * The dmroles of these collections are used as result keys 
	 * @return
	 * @throws Exception 
	 */
	private Map<String, MappingElement> getTableCollections() throws Exception {
		return this.getTableCollections(this.rootElement);
	}

	/**
	 * Return all the children of root being a {@link DataTableCollection} in TABLE mode
	 * The dmroles of these collections are used as result keys 
	 * @param root
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, MappingElement> getTableCollections(MappingElement root) throws Exception {
		List<MappingElement> colls = root.getSubelementsByClass(DataTableCollection.class);
		Map<String, MappingElement> retour = new LinkedHashMap<>();
		for( MappingElement element: colls){
			System.out.println("check " +element.getNodeId().role );
			if( element instanceof DataTableCollection ) {
				System.out.println("add " +element.getNodeId().role );
				retour.put(element.getNodeId().role, element);
			}
		}
		return retour;
	}

	/**
	 * Build map of the collection data grouped by values of the groupByKey.
	 * The primary key is the role, and secondary keys are the groupByKey values
	 * @param tableCollections map of the collections to process. dmroles used as keys
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Map<String, Array>> getGroupedContent(Map<String, MappingElement>  tableCollections) throws Exception {
		Map<String, Map<String, Array>> groupedContents = new LinkedHashMap<>();
		for( String tableCollectionRole: tableCollections.keySet()){	
			System.out.println("getGroupedContent " + tableCollectionRole + " " + tableCollections.get(tableCollectionRole).getNodeId().role);
			Map<String, Array> groupedByContent = tableCollections.get(tableCollectionRole)
					.groupContentByColumnValue(this.groupByKey);
			groupedContents.put(tableCollectionRole, groupedByContent);

		}
		return groupedContents;
	}
	/**
	 * Check that all groupedContent have the same size
	 * @param groupedContents primary key = dmroles and secondary keys are groupByKey values
	 * @throws MappingException  if an inconsistency is detected
	 */
	public void checkGroupSizes(Map<String, Map<String, Array>> groupedContents) throws MappingException{
		int length = -1;
		String refRole = null;
		for( String tableCollectionRole: groupedContents.keySet()){
			int l = groupedContents.get(tableCollectionRole).size();
			if( length == -1) {
				length = l;
				refRole = tableCollectionRole;
			} else if( l != length ){
				throw new UnconsistantResourceException("Sizes of grouped data set do not match: " 
						+ refRole + "=" + length + " " + tableCollectionRole  + "=" + l);
			}
		}
	}
	/**
	 * Check that all groupedContent have the same key sets
	 * @param groupedContents primary key = dmroles and secondary keys are groupByKey values
	 * @throws MappingException  if an inconsistency is detected
	 */
	public void checkGroupKeys(Map<String, Map<String, Array>> groupedContents) throws MappingException{
		String refRole = null;
		Set<String> refKeySet=null;

		for( String tableCollectionRole: groupedContents.keySet()){
			Set<String>  ks = groupedContents.get(tableCollectionRole).keySet();
			if( refKeySet == null) {
				refKeySet = ks;
				refRole = tableCollectionRole;
			} else if( !refKeySet.containsAll(ks) || !ks.containsAll(refKeySet)){
				throw new UnconsistantResourceException("Grouped data have not the same key sets: " 
						+ refRole + " and " + tableCollectionRole);				
			}
		}
	}
	/**
	 * @param groupedContents primary key = dmroles and secondary keys are groupByKey values
	 * @return
	 */
	public Set<String> getKeySet(Map<String, Map<String, Array>> groupedContents){

		for( String tableCollectionRole: groupedContents.keySet()){
			return groupedContents.get(tableCollectionRole).keySet();
		}
		return null;
	}

	/**
	 * Empty the content of all the collections
	 * @param tableCollections collections to be porcessed, dmroles as keys.
	 * @throws Exception 
	 */
	public void removeTableCollectionData(Map<String, MappingElement>  tableCollections) throws Exception{
		for( Entry<String,MappingElement>entry: tableCollections.entrySet()){
			entry.getValue().emptyData();
		}
	}

	/**
	 * Extract from groupedContents the Arrays for a given role and containing all 
	 * value matching the key and put it as content of the Collection children of root having the same dmrole.
	 * @param root Root of the hierarchy of element to be process
	 * @param key groupByKey value
	 * @param groupedContents grouped content of all the Collecionj in TABLE mode of the instance rootElement
	 * @throws Exception 
	 */
	private void setValueByKey( MappingElement root, String key, Map<String, Map<String, Array>> groupedContents) throws Exception {
		Map<String, MappingElement>  tableCollectionClones = this.getTableCollections(root);
		for( String role: tableCollectionClones.keySet() ) {	
			MappingElement tableCollection = tableCollectionClones.get(role);
			MappingElement content = groupedContents.get(role).get(key);
			//tableCollection.setValue(content);
		}
	}


	/**
	 * @param object
	 * @return a deep clone of the object
	 * @throws Exception 
	 */
	public static  MappingElement deepClone(MappingElement object) throws Exception{
		System.out.println(object.getClass());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		ObjectInputStream objectInputStream = new ObjectInputStream(bais);
		return (MappingElement) objectInputStream.readObject();
	}

}

