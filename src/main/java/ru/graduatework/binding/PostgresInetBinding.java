package ru.graduatework.binding;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

public class PostgresInetBinding implements Binding<Object, InetAddress> {

    @Override
    public Converter<Object, InetAddress> converter() {
        return new Converter<>() {

            @Override
            public InetAddress from(Object obj) {
                try {
                    return obj == null ? null : InetAddress.getByName(obj.toString());
                } catch (UnknownHostException e) {
                    throw new RuntimeException("Unknown host: " + obj);
                }
            }

            @Override
            public Object to(InetAddress inetAddress) {
                return inetAddress.getHostAddress();
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<InetAddress> toType() {
                return InetAddress.class;
            }
        };
    }

    @Override
    public void sql(BindingSQLContext<InetAddress> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::inet");
    }

    @Override
    public void register(BindingRegisterContext<InetAddress> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<InetAddress> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    public void set(BindingSetSQLOutputContext<InetAddress> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<InetAddress> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<InetAddress> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetSQLInputContext<InetAddress> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}