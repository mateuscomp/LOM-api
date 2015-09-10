package com.nanuvem.lom.api.tests.entity;

import static com.nanuvem.lom.api.tests.entitytype.EntityTypeHelper.createEntityType;
import static com.nanuvem.lom.api.tests.propertytype.PropertyTypeHelper.createOnePropertyType;
import static org.junit.Assert.fail;

import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.PropertyType;
import com.nanuvem.lom.api.Type;
import com.nanuvem.lom.api.Property;
import com.nanuvem.lom.api.EntityType;
import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.tests.propertytype.PropertyTypeHelper;
import com.nanuvem.lom.api.util.JsonNodeUtil;

public class EntityHelper {

	private static Facade facade;

	public static void setFacade(Facade facade) {
		EntityHelper.facade = facade;
	}

	public static Entity createOneEntity(EntityType entityType,
			String... values) {

		Entity entity = new Entity();
		entity.setEntityType(entityType);

		for (int i = 0; i < values.length; i++) {
			Property property = new Property();
			property.setValue(values[i]);

			if (entityType != null) {
				property.setPropertyType(entityType.getPropertiesTypes().get(i));
			}

			property.setEntity(entity);
			entity.getProperties().add(property);
		}
		return facade.create(entity);
	}

	public static void expectExceptionOnCreateInvalidEntity(
			String entityTypeFullName, String exceptedMessage, String... values) {

		try {
			EntityType entityType = null;
			if (entityTypeFullName != null) {
				entityType = facade
						.findEntityTypeByFullName(entityTypeFullName);
			}

			createOneEntity(entityType, values);
			fail();
		} catch (MetadataException metadataException) {
			Assert.assertEquals(exceptedMessage, metadataException.getMessage());
		}
	}

	public static void createAndVerifyOneEntity(String entityTypeFullName,
			String... values) {

		EntityType entityType = null;
		if (entityTypeFullName != null) {
			entityType = facade.findEntityTypeByFullName(entityTypeFullName);
		}

		int numberOfEntities = facade.findEntitiesByEntityTypeId(
				entityType.getId()).size();

		Entity newEntity = createOneEntity(entityType, values);

		Assert.assertNotNull(newEntity.getId());
		Assert.assertEquals(new Integer(0), newEntity.getVersion());

		Entity createdEntity = facade.findEntityById(newEntity.getId());
		Assert.assertEquals(newEntity, createdEntity);
		Assert.assertEquals(entityTypeFullName, createdEntity.getEntityType()
				.getFullName());

		verifyAllProperties(createdEntity, values);

		List<Entity> entities = facade.findEntitiesByEntityTypeId(entityType
				.getId());

		Assert.assertEquals(numberOfEntities + 1, entities.size());
		Entity listedEntity = entities.get(numberOfEntities);
		Assert.assertEquals(newEntity, listedEntity);
		Assert.assertEquals(entityTypeFullName, listedEntity.getEntityType()
				.getFullName());

		verifyAllProperties(listedEntity, values);

	}

	private static void verifyAllProperties(Entity createdEntity,
			String... values) {

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			Property createdProperty = createdEntity.getProperties().get(i);

			Assert.assertNotNull("Id was null", createdProperty.getId());

			if (usesDefaultConfiguration(value, createdProperty)) {
				validateThatDefaultConfigurationWasAppliedToValue(createdProperty);
			} else {
				Assert.assertEquals(value, createdProperty.getValue());
			}
		}
	}

	private static boolean usesDefaultConfiguration(String value,
			Property createdProperty) {

		return ((value == null) || value.isEmpty())
				&& (createdProperty.getPropertyType().getConfiguration() != null)
				&& (createdProperty.getPropertyType().getConfiguration()
						.contains(PropertyType.DEFAULT_CONFIGURATION_NAME));
	}

	public static Property newProperty(String propertyTypeName,
			String entityTypeFullName, String value, Entity entity) {

		Property property = new Property();
		property.setPropertyType(facade
				.findPropertyTypeByNameAndFullnameEntityType(propertyTypeName,
						entityTypeFullName));
		property.setValue(value);
		property.setEntity(entity);
		return property;
	}

	private static void validateThatDefaultConfigurationWasAppliedToValue(
			Property property) {
		JsonNode jsonNode = null;
		try {
			jsonNode = JsonNodeUtil.validate(property.getPropertyType()
					.getConfiguration(), null);
		} catch (Exception e) {
			fail();
			throw new RuntimeException(
					"Json configuration is in invalid format");
		}
		String defaultField = jsonNode.get(
				PropertyType.DEFAULT_CONFIGURATION_NAME).asText();
		Assert.assertEquals(property.getValue(), defaultField);
	}

	static Property newProperty(String attributeName, String objValue) {
		PropertyType attribute = new PropertyType();
		attribute.setName(attributeName);
		Property value = new Property();
		value.setValue(objValue);
		value.setPropertyType(attribute);
		return value;
	}

	public static void invalidValueForEntity(String entityTypeName,
			Integer sequence, String propertyTypeName, Type type,
			String configuration, String value, String expectedMessage) {

		PropertyTypeHelper.createOnePropertyType(entityTypeName, sequence,
				propertyTypeName, type, configuration);

		EntityHelper.expectExceptionOnCreateInvalidEntity(entityTypeName,
				expectedMessage, value);

	}

	static void updateOneValueOfEntityAndVerifyOneException(
			String namespaceEntityType, String nameEntityType,
			String propertyTypeName, Type type, String configuration,
			String valueOfCreate, String valueOfUpdate,
			String expectedExceptionMessage) {

		try {
			createEntityType(namespaceEntityType, nameEntityType);
			createOnePropertyType(namespaceEntityType
					+ "." + nameEntityType, null, propertyTypeName, type,
					configuration);

			Entity entity = EntityHelper.createOneEntity(
					facade.findEntityTypeByFullName(namespaceEntityType + "." + nameEntityType), valueOfCreate);

			Property property = entity.getProperties().get(0);
			property.setValue(valueOfUpdate);

			facade.update(entity);

		} catch (MetadataException e) {
			Assert.assertEquals(expectedExceptionMessage, e.getMessage());
		}
	}
}