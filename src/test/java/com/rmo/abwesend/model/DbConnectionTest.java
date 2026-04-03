package com.rmo.abwesend.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.DatabaseMetaData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rmo.abwesend.util.Config;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DbConnectionTest {


	@BeforeAll
	static void setToLocal() {
		try {
			Config.readProperties();
		}
		catch (Exception ex) {
			fail("Config Properties lesen");
		}
		Config.setConnectionToLocal();
	}

	/**
	 * Testen ob leere DB geöffent werden kann
	 */
	@Test
	@Order(1)
	void testConnection() {
			try {
				DbConnection.openLocal("");
				DatabaseMetaData meta = DbConnection.getConnection(Config.dbName).getMetaData();
				assertNotNull(meta.getDatabaseMajorVersion(), "Test DB Version");
				assertTrue(!DbConnection.getConnection(Config.dbName).isClosed(), "DB is closed");
			} catch (Exception ex) {
				fail(ex.getMessage());
			}
			try {
				DbConnection.close();
			} catch (Exception ex) {
				fail(ex.getMessage());
			}
	}
}




