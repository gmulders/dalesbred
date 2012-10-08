package fi.evident.dalesbred.dialects;

import fi.evident.dalesbred.JdbcException;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

import static fi.evident.dalesbred.utils.StringUtils.upperCamelToLowerUnderscore;

public class PostgreSQLDialect extends Dialect {

    @Override
    public Object createDatabaseEnum(Enum<?> value) {
        try {
            PGobject object = new PGobject();
            object.setType(upperCamelToLowerUnderscore(value.getClass().getSimpleName()));
            object.setValue(value.name());
            return object;
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }
}