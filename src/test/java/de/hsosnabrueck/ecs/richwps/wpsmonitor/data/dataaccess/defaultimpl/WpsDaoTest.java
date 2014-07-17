/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import de.hsosnabrueck.ecs.richwps.wpsmonitor.create.CreateException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDaoTest {

    public WpsDaoTest() {
    }

    private WpsDataAccess wpsDao;

    private String insertedIds[];

    private static WpsDaoFactory wpsFactory;

    private final static Integer GENERATE_COUNT = 20;

    @BeforeClass
    public static void setUpClass() {
        wpsFactory = new WpsDaoFactory(new WpsDaoDefaultFactory());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        try {

            wpsDao = wpsFactory.create();
            wpsDao.setAutoCommit(false);
            insertedIds = new String[GENERATE_COUNT];

            for (int i = 0; i < GENERATE_COUNT; i++) {
                WpsEntity wps = new WpsEntity(UUID.randomUUID().toString(), "http://" + UUID.randomUUID().toString());
                wpsDao.persist(wps);
                insertedIds[i] = wps.getIdentifier();
            }

        } catch (CreateException ex) {
            fail("Can't create DAO!");
        } catch (MalformedURLException ex) {
            fail(ex.toString());
        } catch (URISyntaxException ex) {
            fail(ex.toString());
        }
    }

    @After
    public void tearDown() {
        wpsDao.rollback();
    }

    /**
     * Test of find method, of class WpsDao.
     */
    @Test
    public void testFind() {
        WpsEntity wps = wpsDao.find(insertedIds[0]);

        Assert.assertTrue(wps != null && wps.getId().equals(wps.getId()));
    }

    /**
     * Test of get method, of class WpsDao.
     */
    @Test
    public void testGet() {
        List<WpsEntity> get = wpsDao.get(new Range(null, GENERATE_COUNT / 2));
        
        Boolean isValid = true;
        for(WpsEntity e : get) {
            isValid = isValid && e != null;
        }
        
        Assert.assertTrue(get.size() == GENERATE_COUNT / 2 && isValid);
    }

    /**
     * Test of getAll method, of class WpsDao.
     */
    @Test
    public void testGetAll() {
        List<WpsEntity> get = wpsDao.getAll();
        
        Boolean isValid = true;
        for(WpsEntity e : get) {
            isValid = isValid && e != null;
        }
        
        Assert.assertTrue(isValid && get.size() >= GENERATE_COUNT);
    }

}
