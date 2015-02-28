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
package de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.defaultimpl;

import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.WpsDaoDefaultFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.jpa.Jpa;
import de.hsos.ecs.richwps.wpsmonitor.creation.CreateException;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.Range;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDaoFactory;
import de.hsos.ecs.richwps.wpsmonitor.data.dataaccess.WpsDataAccess;
import de.hsos.ecs.richwps.wpsmonitor.data.entity.WpsEntity;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class WpsDaoTest {

    private static Jpa jpa;
    private static WpsDaoFactory wpsFactory;
    private static final Integer GENERATE_COUNT = 20;

    @BeforeClass
    public static void setUpClass() {
        jpa = new Jpa("de.hsosnabrueck.ecs.richwps_WPSMonitorTEST_pu");
        jpa.open();

        wpsFactory = new WpsDaoFactory(new WpsDaoDefaultFactory(jpa));
    }

    @AfterClass
    public static void tearDownClass() {
        jpa.close();
    }
    private WpsDataAccess wpsDao;
    private Long[] insertedIds;

    public WpsDaoTest() {
    }

    @Before
    public void setUp() {
        try {

            wpsDao = wpsFactory.create();
            wpsDao.setAutoCommit(false);
            insertedIds = new Long[GENERATE_COUNT];

            for (int i = 0; i < GENERATE_COUNT; i++) {
                WpsEntity wps = new WpsEntity("http://" + UUID.randomUUID().toString());
                wpsDao.persist(wps);
                insertedIds[i] = wps.getId();
            }

        } catch (CreateException ex) {
            fail("Can't create DAO!");
        } catch (MalformedURLException ex) {
            Logger.getLogger(WpsDaoTest.class.getName()).log(Level.SEVERE, null, ex);
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
        for (WpsEntity e : get) {
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
        for (WpsEntity e : get) {
            isValid = isValid && e != null;
        }

        Assert.assertTrue(isValid && get.size() >= GENERATE_COUNT);
    }

    /**
     * Test of find method, of class WpsDao.
     */
    @Test
    public void testFind_String() {
        WpsEntity findById = wpsDao.find(insertedIds[0]);
        WpsEntity findByIdentifier = wpsDao.find(findById.getId());

        Assert.assertTrue(findByIdentifier != null && findById.equals(findByIdentifier));
    }

    /**
     * Test of remove method, of class WpsDao.
     */
    @Test
    public void testRemove_String() {
        WpsEntity findById = wpsDao.find(insertedIds[0]);
        wpsDao.remove(findById);

        WpsEntity compare = wpsDao.find(insertedIds[0]);
        Assert.assertTrue(compare == null);
    }
    
    @Test
    public void findByEndpoint() {
        WpsEntity findById = wpsDao.find(insertedIds[0]);
        WpsEntity founded = wpsDao.find(findById.getEndpoint());

        Assert.assertTrue(findById.equals(founded));
    }
}
