package com.fooorg.fooproj.resources;

import com.fooorg.fooproj.core.FooDAO;
import com.fooorg.fooproj.model.Token;

import lombok.extern.log4j.Log4j;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.After;
import org.junit.Ignore;
import org.mariadb.jdbc.MySQLDataSource;
import org.skife.jdbi.v2.DBI;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import com.fooorg.fooproj.framework.FooProjIntegrationTest;

import org.junit.experimental.categories.Category;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@Log4j
@Category(FooProjIntegrationTest.class)
public class TokensResourceTest {

    DataSource ds;
    TokensResource resource;

    @Before
    public void setup() throws Exception {
        // ds = setupActualDb();
        ds = createAndSetupVolatileDb();
        DBI jdbi = new DBI(ds);
        FooDAO dao = jdbi.onDemand(FooDAO.class);

        resource = new TokensResource(dao);
    }

    @After
    public void teardown() throws Exception {
        destroyVolatileDb(ds);
    }

    @Test
    public void fetchTokenById() {
        // Arrange
        int key = 180130466;
        String value = UUID.randomUUID().toString();
        resource.addToken(new Token(key, value));
        // Act
        Token t = resource.listTokenById(key);
        // Assert
        assertNotNull(t);
        assertEquals(key, t.getId());
        assertEquals(value, t.getToken());
    }

    @Test
    public void fetchAllTokens() {
        // Arrange
        IntStream.range(0, 82).forEach(i -> {
            resource.addToken(new Token(
                Math.abs(ThreadLocalRandom.current().nextInt()),
                UUID.randomUUID().toString()));
        });
        // Act
        Collection<Token> tokens = resource.listTokens();
        // Assert
        assertNotNull(tokens);
        assertEquals(82, tokens.size());
    }

    @Test
    public void createToken() {
        // Arrange
        // Act
        resource.addToken(new Token(
                Math.abs(ThreadLocalRandom.current().nextInt()),
                UUID.randomUUID().toString()));
        // Assert
    }

    // TODO : Move this to base framework
    // VOLATILE JdbcDataSource
    private DataSource createAndSetupVolatileDb() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setUser("sample");
        ds.setPassword("insecure");
        ds.setUrl("jdbc:h2:mem:mytestdb;MODE=MYSQL;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1");

        Connection conn = ds.getConnection();
        try {
            RunScript.execute(conn, new FileReader(
                new File(getClass().getResource("/sql/sampleservice-test-h2.ddl").toURI())));
        } catch (Exception e) {
            log.error("Some SQLException", e);
            throw e;
        } finally {
            if(conn != null) {
                conn.close();
            }
        }

        return ds;
    }

    private void destroyVolatileDb(DataSource ds) throws SQLException {
        if(!(ds instanceof JdbcDataSource)) {
            return;
        }
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.createStatement().execute("SHUTDOWN");
        } catch (Exception ex) {
            // Nothing to do
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
    }

    private DataSource setupActualDb() {
        MySQLDataSource ds = new MySQLDataSource();
        ds.setUserName("sample");
        ds.setPassword("insecure");
        ds.setUrl("jdbc:mysql://localhost:3306/sampleservice_test");
        return ds;
    }
}