package com.nanuvem.lom.api.tests;

import org.junit.After;
import org.junit.Before;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.entity.InstanceHelper;
import com.nanuvem.lom.api.tests.entitytype.EntityHelper;
import com.nanuvem.lom.api.tests.propertytype.AttributeHelper;

public abstract class LomTestCase {

	protected static final String ENTITY_ALREADY_EXISTS = "The %1$s Entity already exists";
	protected static final String ENTITY_NAME_IS_MANDATORY = "The name of an Entity is mandatory";
	protected static final String INVALID_VALUE_FOR_ENTITY = "Invalid value for Entity %1$s: %2$s";

	protected static final String INVALID_VALUE_FOR_ATTRIBUTE = "Invalid value for Attribute %1$s: %2$s";

	protected Facade facade;

	public abstract Facade createFacade();

	@Before
	public void init() {
		facade = createFacade();
		EntityHelper.setFacade(facade);
		AttributeHelper.setFacade(facade);
		InstanceHelper.setFacade(facade);

		this.facade.getDaoFactory().createDatabaseSchema();
	}

	@After
	public void finalizze() {
		this.facade.getDaoFactory().dropDatabaseSchema();
	}

}
