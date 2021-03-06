package com.nanuvem.lom.api.tests.entity;

import static com.nanuvem.lom.api.tests.entity.EntityHelper.createAndSaveOneEntity;
import static com.nanuvem.lom.api.tests.entity.EntityHelper.createEntity;
import static com.nanuvem.lom.api.tests.entity.EntityHelper.expectExceptionOnInvalidEntityList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.tests.LomTestCase;

public abstract class ReadEntityTest extends LomTestCase {


    @Test
    public void listallEntities() {
        List<Entity> allEntities = facade.listAllEntities();
        Assert.assertEquals(0, allEntities.size());

        Entity entity1 = createAndSaveOneEntity("ns1", "n1");
        Entity entity2 = createAndSaveOneEntity("ns2", "n2");
        Entity entity3 = createAndSaveOneEntity("ns2", "n3");

        allEntities = facade.listAllEntities();

        Assert.assertEquals(3, allEntities.size());
        Assert.assertEquals(entity1, allEntities.get(0));
        Assert.assertEquals(entity2, allEntities.get(1));
        Assert.assertEquals(entity3, allEntities.get(2));

        facade.deleteEntity(entity1.getId());
        facade.deleteEntity(entity2.getId());
        facade.deleteEntity(entity3.getId());

        Assert.assertEquals(0, facade.listAllEntities().size());
    }

    @Test
    public void listEntitiesByValidFragmentOfNameAndPackage() {
        String namespaceFragment = "ns";
        String nameFragment = "n";

        List<Entity> allEntities = facade.listEntitiesByFullName(namespaceFragment);
        Assert.assertEquals(0, allEntities.size());

        allEntities = facade.listEntitiesByFullName(nameFragment);
        Assert.assertEquals(0, allEntities.size());

        Entity entity1 = createAndSaveOneEntity("ns1", "n1");
        Entity entity2 = createAndSaveOneEntity("ns2", "n2");
        Entity entity3 = createAndSaveOneEntity("ns2", "n3");

        allEntities = facade.listEntitiesByFullName(namespaceFragment);
        Assert.assertEquals(3, allEntities.size());
        Assert.assertEquals(entity1, allEntities.get(0));
        Assert.assertEquals(entity2, allEntities.get(1));
        Assert.assertEquals(entity3, allEntities.get(2));

        allEntities = facade.listEntitiesByFullName(nameFragment);
        Assert.assertEquals(3, allEntities.size());
        Assert.assertEquals(entity1, allEntities.get(0));
        Assert.assertEquals(entity2, allEntities.get(1));
        Assert.assertEquals(entity3, allEntities.get(2));

        facade.deleteEntity(entity1.getId());
        facade.deleteEntity(entity2.getId());
        facade.deleteEntity(entity3.getId());

        Assert.assertEquals(0, facade.listAllEntities().size());
    }

    @Test
    public void listEntitiesByEmptyNameAndPackage() {
        List<Entity> allEntities = facade.listEntitiesByFullName("");
        Assert.assertEquals(0, allEntities.size());

        Entity entity1 = createAndSaveOneEntity("ns1", "n1");
        Entity entity2 = createAndSaveOneEntity("ns2", "n2");
        Entity entity3 = createAndSaveOneEntity("ns2", "n3");

        List<Entity> allEntities1 = facade.listEntitiesByFullName("");
        Assert.assertEquals(3, allEntities1.size());
        Assert.assertEquals(entity1, allEntities1.get(0));
        Assert.assertEquals(entity2, allEntities1.get(1));
        Assert.assertEquals(entity3, allEntities1.get(2));

        allEntities = facade.listEntitiesByFullName("nspace");
        Assert.assertEquals(0, allEntities.size());

        facade.deleteEntity(entity1.getId());
        facade.deleteEntity(entity2.getId());
        facade.deleteEntity(entity3.getId());

        Assert.assertEquals(0, facade.listAllEntities().size());
    }

    @Test
    public void listEntitiesByFragmentOfNameAndPackagesWithSpaces() {
        expectExceptionOnInvalidEntityList("na me", "Invalid value for Entity full name: na me");
    }

    @Test
    public void listEntitiesForcingCaseInsensitivePackagesAndNames() {
        Entity entity1 = createAndSaveOneEntity("ns1", "n1");
        Entity entity2 = createAndSaveOneEntity("NS2", "n2");
        Entity entity3 = createAndSaveOneEntity("NS3", "N3");
        List<Entity> expectedEntities = new ArrayList<Entity>();
        expectedEntities.add(entity1);
        expectedEntities.add(entity2);
        expectedEntities.add(entity3);

        List<Entity> allEntities1 = facade.listEntitiesByFullName("ns");
        Assert.assertEquals(3, allEntities1.size());
        Assert.assertEquals(entity1, allEntities1.get(0));
        Assert.assertEquals(entity2, allEntities1.get(1));
        Assert.assertEquals(entity3, allEntities1.get(2));

        allEntities1 = facade.listEntitiesByFullName("n");
        Assert.assertEquals(3, allEntities1.size());
        Assert.assertEquals(entity1, allEntities1.get(0));
        Assert.assertEquals(entity2, allEntities1.get(1));
        Assert.assertEquals(entity3, allEntities1.get(2));

        allEntities1 = facade.listEntitiesByFullName("NS");
        Assert.assertEquals(3, allEntities1.size());
        Assert.assertEquals(entity1, allEntities1.get(0));
        Assert.assertEquals(entity2, allEntities1.get(1));
        Assert.assertEquals(entity3, allEntities1.get(2));

        allEntities1 = facade.listEntitiesByFullName("N");
        Assert.assertEquals(3, allEntities1.size());
        Assert.assertEquals(entity1, allEntities1.get(0));
        Assert.assertEquals(entity2, allEntities1.get(1));
        Assert.assertEquals(entity3, allEntities1.get(2));
    }

    @Test
    public void listEntitiesUsingInvalidFragmentOfNameAndPackage() {
        expectExceptionOnInvalidEntityList("n$", INVALID_VALUE_FOR_ENTITY, "full name", "n$");
        expectExceptionOnInvalidEntityList("n#", INVALID_VALUE_FOR_ENTITY, "full name", "n#");
        expectExceptionOnInvalidEntityList("n=", INVALID_VALUE_FOR_ENTITY, "full name", "n=");
        expectExceptionOnInvalidEntityList("n'", INVALID_VALUE_FOR_ENTITY, "full name", "n'");
        expectExceptionOnInvalidEntityList("n/n", INVALID_VALUE_FOR_ENTITY, "full name", "n/n");
        expectExceptionOnInvalidEntityList("n*", INVALID_VALUE_FOR_ENTITY, "full name", "n*");
    }

    @Test
    public void getEntityByValidNameAndPackage() {
        Assert.assertEquals(0, facade.listEntitiesByFullName("ns.n").size());

        Entity entity1 = createEntity("ns1", "n1");
        Entity foundEntity1 = facade.findEntityByFullName("ns1.n1");
        Assert.assertEquals(entity1, foundEntity1);

        Entity entity2 = createEntity("ns2", "n2");
        Entity foundEntity2 = facade.findEntityByFullName("ns2.n2");
        Assert.assertEquals(entity2, foundEntity2);

        Assert.assertEquals(1, facade.listEntitiesByFullName("ns1.n").size());
        Assert.assertEquals(0, facade.listEntitiesByFullName("ns.n1").size());
        Assert.assertEquals(0, facade.listEntitiesByFullName("ns2.n1").size());

        List<Entity> allEntities = facade.listAllEntities();
        Assert.assertEquals(2, allEntities.size());
        Assert.assertEquals(entity1, allEntities.get(0));
        Assert.assertEquals(entity2, allEntities.get(1));
    }

    @Test
    public void getEntityByEmptyNameAndPackage() {
        createEntity("ns1", "n1");
        Entity entity2 = createEntity(null, "n2");
        Assert.assertEquals(1, facade.listEntitiesByFullName(".n1").size());

        Entity foundEntity2 = facade.findEntityByFullName("n2");
        Assert.assertEquals(entity2, foundEntity2);
        Assert.assertEquals(1, facade.listEntitiesByFullName("ns1.").size());
    }

    @Test
    public void getEntityByNameAndPackageWithSpaces() {
        expectExceptionOnInvalidEntityList(".na me", INVALID_VALUE_FOR_ENTITY, "full name", ".na me");
        expectExceptionOnInvalidEntityList("name space.name", INVALID_VALUE_FOR_ENTITY, "full name", "name space.name");
        expectExceptionOnInvalidEntityList("namespace.na me", INVALID_VALUE_FOR_ENTITY, "full name", "namespace.na me");
    }

    @Test
    public void getEntityForcingCaseInsensitivePackagesAndNames() {
        Entity entity = createEntity("nS", "nA");
        Entity ea = facade.findEntityByFullName("ns.na");
        Assert.assertEquals(entity, ea);

        ea = facade.findEntityByFullName("NS.NA");
        Assert.assertEquals(entity, ea);

        ea = facade.findEntityByFullName("nS.nA");
        Assert.assertEquals(entity, ea);

        ea = facade.findEntityByFullName("NS.na");
        Assert.assertEquals(entity, ea);

        ea = facade.findEntityByFullName("ns.NA");
        Assert.assertEquals(entity, ea);

        ea = facade.findEntityByFullName("Ns.Na");
        Assert.assertEquals(entity, ea);

    }

    @Test
    public void getEntityUsingInvalidNameAndPackage() {
        expectExceptionOnInvalidEntityList("ns.n$", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n$");
        expectExceptionOnInvalidEntityList("ns.n#", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n#");
        expectExceptionOnInvalidEntityList("ns.n=", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n=");
        expectExceptionOnInvalidEntityList("ns.n/n", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n/n");
        expectExceptionOnInvalidEntityList("ns.n*", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n*");
        expectExceptionOnInvalidEntityList("ns.n'", INVALID_VALUE_FOR_ENTITY, "full name", "ns.n'");
        expectExceptionOnInvalidEntityList("ns$.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns$.n");
        expectExceptionOnInvalidEntityList("ns#.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns#.n");
        expectExceptionOnInvalidEntityList("ns=.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns=.n");
        expectExceptionOnInvalidEntityList("ns/.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns/.n");
        expectExceptionOnInvalidEntityList("ns*.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns*.n");
        expectExceptionOnInvalidEntityList("ns'.n", INVALID_VALUE_FOR_ENTITY, "full name", "ns'.n");
    }

}
