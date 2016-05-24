package hms.wikidata.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class WikidataAPI {

	/**
	 * Native SQL Queries
	 */
	private static final String SELECT_DESCRIPTION = "SELECT value FROM description where entity_id = ? and language =?";
	private static final String SELECT_ALIASES = "select * from alias where entity_id = ?  and language =?";
	private static final String SELECT_LABEL = "SELECT value FROM label where entity_id = ? and language =?";
	private static final String SELECT_PARENT_PROPERTY = "SELECT entity_id as domain, simple_value as parent FROM wikidata_20150928.wiki_claims  where claim_id = 'P1647' and entity_id = ?";
	private static final String SELECT_PROPERTY_ARG1_INSTANCES = "SELECT entity_id as domain FROM wikidata_20150928.wiki_claims where claim_id = ? limit ?";
	private static final String SELECT_PROPERTY_ARG2_INSTANCES = "SELECT wikidata_item_value as val FROM wikidata_20150928.wiki_claims where claim_id = ? limit ?";
	private static final String SELECT_PARENT_CLASS = "SELECT wikidata_item_value as val FROM wikidata_20150928.wiki_claims where claim_id = 'P31' and entity_id = ?";

	private static final String SELECT_SUBJECT_OF_PROPERTY = "SELECT distinct wikidata_item_value as sub FROM wikidata_20150928.wiki_claims where claim_id = 'P1629' and entity_id = ?";

	private static final String SELECT_INSTANCES_OF_ENTITY = "SELECT entity_id as inst FROM wikidata_20150928.wiki_claims where claim_id = 'P31' and wikidata_item_value = ?";

	private static final String SELECT_PROPERTY_TALK_REPRESENTS = "SELECT represents FROM wikidata_20150928.property_talk where propID = ?";
	private static final String SELECT_PROPERTY_TALK_DOMAIN = "SELECT domain FROM wikidata_20150928.property_talk where propID = ?";
	private static final String SELECT_PROPERTY_TALK_ALLOWED_VALUES = "SELECT allowed_values FROM wikidata_20150928.property_talk where propID = ?";

	/**
	 * Get the description of a given Wikidata entity (item or property)
	 * 
	 * @param entityID
	 * @param lang
	 * @return
	 */
	public static String getDescription(String entityID, WikidataLanguages lang) {

		String desc = null;

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_DESCRIPTION);
			st.setString(1, entityID);
			st.setString(2, lang.name());
			ResultSet result = st.executeQuery();

			if (result.next()) {

				desc = result.getString("value");
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return desc;

	}

	/**
	 * Get the label of a given Wikidata entity (item or property)
	 * 
	 * @param entityID
	 * @param lang
	 * @return
	 */
	public static String getLabel(String entityID, WikidataLanguages lang) {

		String label = null;

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_LABEL);
			st.setString(1, entityID);
			st.setString(2, lang.name());
			ResultSet result = st.executeQuery();

			if (result.next()) {

				label = result.getString("value").replace("/", " ");

				if (label.length() < 1) {

					return null;

				}

			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return label;

	}

	/**
	 * Get a list of aliases for a given Wikidata entity (item or property)
	 * 
	 * @param entityID
	 * @param lang
	 * @return
	 */
	public static Collection<String> getAliases(String entityID, WikidataLanguages lang) {

		Collection<String> aliases = new ArrayList<String>();
		PreparedStatement st;
		try {
			st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_ALIASES);
			st.setString(1, entityID);
			st.setString(2, lang.name());
			ResultSet result = st.executeQuery();

			while (result.next()) {

				String alias = result.getString("value");
				if (alias.length() > 1) {

					if (alias.contains("/")) {
						String[] aliasParts = alias.split("/");
						for (String a : aliasParts) {
							aliases.add(a);
						}
					} else {
						aliases.add(alias.replace("(", "").replace(")", ""));
					}

				}

			}
			result.close();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return aliases;

	}

	/**
	 * Get the parent property of a given property (subproperty of)
	 * 
	 * @param propID
	 * @param lang
	 * @return
	 */
	public static String getParentProperty(String propID) {

		String parentID = null;

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_PARENT_PROPERTY);
			st.setString(1, propID);

			ResultSet result = st.executeQuery();

			if (result.next()) {

				parentID = result.getString("parent");
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parentID;

	}

	/**
	 * Get the parent property of a given property (subproperty of)
	 * 
	 * @param propID
	 * @param lang
	 * @return
	 */
	public static String getSubjectOfProperty(String propID) {
		String subjectID = null;

		if (propID.startsWith("P")) { // ensure that we are dealing with a
										// property

			try {
				PreparedStatement st = WikidataDBConnector.getDBConnectin()
						.prepareStatement(SELECT_SUBJECT_OF_PROPERTY);
				st.setString(1, propID);

				ResultSet result = st.executeQuery();

				if (result.next()) {

					subjectID = result.getString("sub");
				}
				result.close();
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Please give a valid property ID " + propID);
		}

		return subjectID;

	}

	/**
	 * Get IDs of items which are instances of ARG1 (subject) of a given
	 * property
	 * 
	 * @param propID
	 * @param max
	 * @return
	 */
	public static Collection<String> getPropertyARG1Instances(String propID, int max) {

		Collection<String> instanceIDs = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin()
					.prepareStatement(SELECT_PROPERTY_ARG1_INSTANCES);
			st.setString(1, propID);
			st.setInt(2, max);

			ResultSet result = st.executeQuery();

			while (result.next()) {

				instanceIDs.add(result.getString("domain"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instanceIDs;

	}

	/**
	 * Get IDs of items which are instances of ARG2 (object) of a given property
	 * 
	 * @param propID
	 * @param max
	 * @return
	 */
	public static Collection<String> getPropertyARG2Instances(String propID, int max) {

		Collection<String> instanceIDs = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin()
					.prepareStatement(SELECT_PROPERTY_ARG2_INSTANCES);
			st.setString(1, propID);
			st.setInt(2, max);

			ResultSet result = st.executeQuery();

			while (result.next()) {

				instanceIDs.add(result.getString("val"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instanceIDs;

	}

	/**
	 * Get parent classes of a given entity
	 * 
	 * @param entityID
	 * @return
	 */
	public static Collection<String> getParentClasses(String entityID) {

		Collection<String> parentClasses = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_PARENT_CLASS);
			st.setString(1, entityID);

			ResultSet result = st.executeQuery();

			while (result.next()) {

				parentClasses.add(result.getString("val"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parentClasses;

	}

	/**
	 * Get instances of given entity
	 * 
	 * @param entityID
	 * @return
	 */
	public static Collection<String> getInstances(String entityID) {

		Collection<String> instanceList = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin().prepareStatement(SELECT_INSTANCES_OF_ENTITY);
			st.setString(1, entityID);

			ResultSet result = st.executeQuery();

			while (result.next()) {

				instanceList.add(result.getString("inst"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instanceList;
	}

	/**
	 * Get the types of ARG1 of a given property (the labels of the types are
	 * provided)
	 * 
	 * @param propID
	 * @param maxInst
	 * @param lang
	 * @return
	 */
	public static Collection<String> getPropertyARG1Classes(String propID, int maxInst, WikidataLanguages lang) {

		Collection<String> arg1Instances = getPropertyARG1Instances(propID, maxInst);

		Collection<String> arg1Classes = new HashSet<>();

		for (String inst : arg1Instances) {

			Collection<String> parentClasses = getParentClasses(inst);

			for (String parent : parentClasses) {
				String classLabel = getLabel(parent, lang);
				if (classLabel != null) {
					arg1Classes.add(classLabel.toLowerCase());
				}
			}

		}
		return arg1Classes;
	}

	/**
	 * Get the types of ARG2 of a given property (the labels of the types are
	 * provided)
	 * 
	 * @param propID
	 * @param maxInst
	 * @param lang
	 * @return
	 */
	public static Collection<String> getPropertyARG2Classes(String propID, int maxInst, WikidataLanguages lang) {

		Collection<String> arg2Instances = getPropertyARG2Instances(propID, maxInst);

		Collection<String> arg2Classes = new HashSet<>();

		for (String inst : arg2Instances) {

			Collection<String> parentClasses = getParentClasses(inst);

			for (String parent : parentClasses) {
				String classLabel = getLabel(parent, lang);
				if (classLabel != null) {
					arg2Classes.add(classLabel.toLowerCase());
				}
			}

		}
		return arg2Classes;
	}

	/**
	 * Get the list of itmes that are represented by this property from the talk
	 * page. Normally this corresponds to the value of subjectItemOfThisProperty
	 * 
	 * @param propertyID
	 * @return
	 */
	public static Collection<String> getPropertyRepresentingItems(String propertyID) {

		if (!propertyID.startsWith("P")) {

			throw new RuntimeException("Enter a valid property ID. You entered [" + propertyID + "]");
		}

		Collection<String> representinItemIDs = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin()
					.prepareStatement(SELECT_PROPERTY_TALK_REPRESENTS);
			st.setString(1, propertyID);

			ResultSet result = st.executeQuery();

			if (result.next()) {
				String itemIDsStr = result.getString("represents");
				if (itemIDsStr != null && !itemIDsStr.equals("")) {

					String[] itemIDsArr = itemIDsStr.split(",");
					for (String id : itemIDsArr) {
						representinItemIDs.add(id.trim());
					}
				}
			}
			result.close();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return representinItemIDs;
	}

	/**
	 * Get the list of items that are domains (ARG1 types) for this property from the talk page
	 * @param propertyID
	 * @return
	 */
	public static Collection<String> getPropertyDomainItems(String propertyID) {

		if (!propertyID.startsWith("P")) {

			throw new RuntimeException("Enter a valid property ID. You entered [" + propertyID + "]");
		}

		Collection<String> domainItemIDs = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin()
					.prepareStatement(SELECT_PROPERTY_TALK_DOMAIN);
			st.setString(1, propertyID);

			ResultSet result = st.executeQuery();

			if (result.next()) {
				String itemIDsStr = result.getString("domain");
				if (itemIDsStr != null && !itemIDsStr.equals("")) {

					String[] itemIDsArr = itemIDsStr.split(",");
					for (String id : itemIDsArr) {
						domainItemIDs.add(id.trim());
					}
				}
			}
			result.close();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return domainItemIDs;
	}
	
	
	/**
	 * Get the list of items that can be used as values (ARG2 types) for this property from the talk page
	 * @param propertyID
	 * @return
	 */
	public static Collection<String> getPropertyAllowedValuesItems(String propertyID) {

		if (!propertyID.startsWith("P")) {

			throw new RuntimeException("Enter a valid property ID. You entered [" + propertyID + "]");
		}

		Collection<String> allowedValuesItemIDs = new HashSet<String>();

		try {
			PreparedStatement st = WikidataDBConnector.getDBConnectin()
					.prepareStatement(SELECT_PROPERTY_TALK_ALLOWED_VALUES);
			st.setString(1, propertyID);

			ResultSet result = st.executeQuery();

			if (result.next()) {
				String itemIDsStr = result.getString("allowed_values");
				if (itemIDsStr != null && !itemIDsStr.equals("")) {

					String[] itemIDsArr = itemIDsStr.split(",");
					for (String id : itemIDsArr) {
						allowedValuesItemIDs.add(id.trim());
					}
				}
			}
			result.close();
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allowedValuesItemIDs;
	}

	public static void main(String[] args) {

		String entityID = "P157";
		// System.out.println(getLabel(entityID, WikidataLanguages.en));
		// System.out.println(getDescription(entityID, WikidataLanguages.en));
		// System.out.println(getAliases(entityID, WikidataLanguages.en));
		// System.out.println(".......");
		// System.out.println(getParentProperty(entityID));
		// System.out.println(getLabel(getParentProperty(entityID),WikidataLanguages.en));
		// System.out.println(getDescription(getParentProperty(entityID),WikidataLanguages.en));
		// System.out.println(getAliases(getParentProperty(entityID),WikidataLanguages.en));
		//
		// System.out.println("....................");
		//
		// System.out.println(getPropertyARG1Classes(entityID, 1000,
		// WikidataLanguages.en));
		//
		// System.out.println(getPropertyARG2Classes(entityID, 1000,
		// WikidataLanguages.en));
		//
		// String subjectOfProperty = getSubjectOfProperty(entityID);
		//
		// System.out.println(getLabel(subjectOfProperty,
		// WikidataLanguages.en));
		// System.out.println(getAliases(subjectOfProperty,
		// WikidataLanguages.en));
		//
		// // Get instances of the subject
		// Collection<String> intsanceIDs = getInstances(subjectOfProperty);
		// for (String inst : intsanceIDs) {
		//
		// System.out.println(inst + ": " + getLabel(inst,
		// WikidataLanguages.en));
		// }

		System.out.println(getPropertyRepresentingItems(entityID));
		System.out.println(getPropertyDomainItems(entityID));
		
		System.out.println(getPropertyAllowedValuesItems(entityID));


	}

	// private static void test1(String entityID,boolean arg1, int max) {
	// System.out.println("------ instances -----");
	// Collection<String> instArg = null;
	// if(arg1){
	// instArg = getPropertyARG1Instances(entityID, max);
	// }
	// else{
	// instArg = getPropertyARG2Instances(entityID, max);
	// }
	//
	// for(String inst : instArg){
	// System.out.println("INSTANCE: " + inst + "\t" + getLabel(inst,
	// WikidataLanguages.en));
	// Collection<String> parents = getParentClasses(inst);
	// for(String p : parents){
	// System.out.println("\t Parent: " + p + "\t" + getLabel(p,
	// WikidataLanguages.en));
	// }
	//
	// }
	// }

}
