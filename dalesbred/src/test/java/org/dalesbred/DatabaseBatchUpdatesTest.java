/*
 * Copyright (c) 2015 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dalesbred;

import org.dalesbred.result.ResultSetProcessor;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.dalesbred.query.SqlQuery.query;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DatabaseBatchUpdatesTest {

    private final Database db = TestDatabaseProvider.createInMemoryHSQLDatabase();

    @Rule
    public final TransactionalTestsRule rule = new TransactionalTestsRule(db);

    @Test
    public void batchUpdate() {
        db.update("drop table if exists dictionary");
        db.update("create temporary table dictionary (word varchar(64) primary key)");

        List<List<String>> data = new ArrayList<>();
        data.add(singletonList("foo"));
        data.add(singletonList("bar"));
        data.add(singletonList("baz"));
        int[] result = db.updateBatch("insert into dictionary (word) values (?)", data);

        assertThat(result, is(new int[] { 1, 1, 1 }));
        assertThat(db.findAll(String.class, "select word from dictionary order by word"), is(asList("bar", "baz", "foo")));
    }

    @Test
    public void batchUpdateWithGeneratedKeys() {
        db.update("drop table if exists my_table");
        db.update("create temporary table my_table (id identity primary key, str varchar(64), num int)");

        List<List<?>> argLists = asList(
                asList("foo", 1),
                asList("bar", 2),
                asList("baz", 3));

        List<Integer> result = db.updateBatchAndProcessGeneratedKeys(new CollectKeysResultSetProcessor(), singletonList("ID"), "INSERT INTO my_table (str, num) VALUES (?,?)", argLists);

        assertThat(result, is(asList(0, 1, 2)));
    }

    @Test
    public void exceptionsContainReferenceToOriginalQuery() {
        List<List<String>> data = new ArrayList<>();
        data.add(singletonList("foo"));

        try {
            db.updateBatch("insert into nonexistent_table (foo) values (?)", data);
            fail("Expected DatabaseException");
        } catch (DatabaseException e) {
            assertThat(e.getQuery(), is(query("insert into nonexistent_table (foo) values (?)", "<batch-update>")));
        }
    }

    private static final class CollectKeysResultSetProcessor implements ResultSetProcessor<List<Integer>> {
        @Override
        public List<Integer> process(@NotNull ResultSet resultSet) throws SQLException {
            List<Integer> result = new ArrayList<>();
            while (resultSet.next())
                result.add(resultSet.getInt(1));
            return result;
        }
    }
}
