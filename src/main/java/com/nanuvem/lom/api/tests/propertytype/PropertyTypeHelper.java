package com.nanuvem.lom.api.tests.propertytype;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import com.nanuvem.lom.api.PropertyType;
import com.nanuvem.lom.api.Type;
import com.nanuvem.lom.api.EntityType;
import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.MetadataException;

public class PropertyTypeHelper {

	private static Facade facade;

	public static final Type TEXT = Type.TEXT;
	public static final Type LONGTEXT = Type.LONGTEXT;
	public static final Type PASSWORD = Type.PASSWORD;

	static final String MANDATORY_FALSE = "{\"mandatory\":false}";
	static final String MANDATORY_TRUE = "{\"mandatory\":true}";

	static final String INVALID_SEQUENCE = "Invalid value for Attribute sequence: %1$s";
	static final String ATTRIBUTE_IS_MANDATORY = "The %1$s of an Attribute is mandatory";
	static final String ATTRIBUTE_DUPLICATION = "Attribute duplication on %1$s Entity. It already has an attribute %2$s.";
	static final String ENTITY_NOT_FOUND = "Entity not found: %1$s";

	public static void setFacade(Facade facade) {
		PropertyTypeHelper.facade = facade;
	}

	public static PropertyType createOnePropertyType(String entityTypeFullName,
			Integer propertyTypeSequence, String propertyTypeName, Type type,
			String propertyTypeConfiguration) {

		EntityType entityType = facade
				.findEntityTypeByFullName(entityTypeFullName);
		PropertyType propertyType = new PropertyType();
		propertyType.setName(propertyTypeName);

		propertyType.setEntityType(entityType);
		propertyType.setSequence(propertyTypeSequence);
		propertyType.setType(type);
		propertyType.setConfiguration(propertyTypeConfiguration);
		propertyType = facade.create(propertyType);

		return propertyType;
	}

	public static void expectExceptionOnCreateInvalidPropertyType(
			String entityTypeFullName, Integer propertyTypeSequence,
			String propertyTypeName, Type type,
			String propertyTypeConfiguration, String expectedMessage,
			String... args) {

		try {
			createOnePropertyType(entityTypeFullName, propertyTypeSequence,
					propertyTypeName, type, propertyTypeConfiguration);
			fail();
		} catch (MetadataException e) {
			String formatedMessage = String.format(expectedMessage,
					(Object[]) args);
			Assert.assertEquals(formatedMessage, e.getMessage());
		}
	}

	public static void createAndVerifyOnePropertyType(
			String entityTypeFullName, Integer propertyTypeSequence,
			String propertyTypeName, Type type, String propertyTypeConfiguration) {

		PropertyType createdPropertyType = createOnePropertyType(
				entityTypeFullName, propertyTypeSequence, propertyTypeName,
				type, propertyTypeConfiguration);

		Assert.assertNotNull(createdPropertyType.getId());
		Assert.assertEquals(new Integer(0), createdPropertyType.getVersion());
//		Assert.assertEquals(createdPropertyType,
//				facade.findPropertyTypeById(createdPropertyType.getId()));
	}

	public static PropertyType updateAttribute(String fullnameEntityType,
			PropertyType oldPropertyType, Integer newSequence, String newName,
			Type newType, String newConfiguration) {

		PropertyType propertyType = facade
				.findPropertyTypeByNameAndFullnameEntityType(
						oldPropertyType.getName(), fullnameEntityType);

		propertyType.setSequence(newSequence);
		propertyType.setName(newName);
		propertyType.setType(newType);
		propertyType.setConfiguration(newConfiguration);
		propertyType.setId(oldPropertyType.getId());
		propertyType.setVersion(oldPropertyType.getVersion());

		return facade.update(propertyType);
	}

	public static void expectExceptionOnUpdateInvalidPropertyType(
			String fullnameEntityType, PropertyType oldPropertyType,
			Integer newSequence, String newName, Type newType,
			String newConfiguration, String exceptedMessage) {

		try {
			updateAttribute(fullnameEntityType, oldPropertyType, newSequence,
					newName, newType, newConfiguration);
			fail();
		} catch (MetadataException metadataException) {
			Assert.assertEquals(exceptedMessage, metadataException.getMessage());
		}
	}

	public static void expectExceptionOnUpdateWithInvalidNewName(
			PropertyType createdPropertyType, String invalidNewName,
			String exceptedMessage) {
		
		expectExceptionOnUpdateInvalidPropertyType("abc.a",
				createdPropertyType, 1, invalidNewName, LONGTEXT, null,
				exceptedMessage);
	}

	public static void verifyUpdatedPropertyType(
			PropertyType propertyTypeBeforeUpgrade, PropertyType updatedPropertyType) {

		Assert.assertNotNull("updatedAttribute.id should not be null",
				updatedPropertyType.getId());

		int versionIncrementedInCreatePropertyType = propertyTypeBeforeUpgrade
				.getVersion() + 1;
		Assert.assertEquals("updatedAttribute.version should be "
				+ versionIncrementedInCreatePropertyType,
				versionIncrementedInCreatePropertyType,
				(int) updatedPropertyType.getVersion());

		PropertyType listedPropertyType = facade
				.findPropertyTypeById(propertyTypeBeforeUpgrade.getId());
		Assert.assertEquals("listedAttribute should be to updatedAttribute",
				updatedPropertyType, listedPropertyType);

		Assert.assertFalse("listedAttribute should be to createdAttribute",
				propertyTypeBeforeUpgrade.equals(listedPropertyType));
	}

}
