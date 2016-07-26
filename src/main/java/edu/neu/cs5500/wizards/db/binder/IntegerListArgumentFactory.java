package edu.neu.cs5500.wizards.db.binder;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class IntegerListArgumentFactory implements ArgumentFactory<List<Integer>> {
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof List;
    }

    @Override
    public Argument build(Class<?> expectedType, final List<Integer> value, StatementContext ctx) {
        return new Argument() {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                Array array = ctx.getConnection().createArrayOf("int", value.toArray());
                statement.setArray(position, array);
            }
        };
    }
}
