package org.typemeta.funcj.control;

import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions.F;

import java.util.*;
import java.util.concurrent.*;

public class TaglessFinalTest {

    public interface FlatMap<HKT, T> {
        <U> FlatMap<HKT, U> flatMap(F<T, FlatMap<HKT, U>> f);
        <U> FlatMap<HKT, U> map(F<T, U> f);
    }

    static class User {
        public final long id;
        public final String name;

        User(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class DatabaseError extends Throwable {}

    static class ErrorFindingUser extends DatabaseError {}

    static class ErrorUpdatingUser extends DatabaseError {}

    static class ErrorDeletingUser extends DatabaseError {
        public final String msg;

        ErrorDeletingUser(String msg) {
            this.msg = msg;
        }
    }

    interface DatabaseAlgebra<M, T> {
        FlatMap<M, Boolean> create(T t);

        FlatMap<M, Either<DatabaseError, T>> read(long id);

        FlatMap<M, Either<DatabaseError, Unit>> delete(long id);
    }

    public static class Future<T> implements FlatMap<Future.t, T> {
        public static <T> Future<T> pure(T value) {
            return new Future<T>(value);
        }

        public static <A> Future<A> prj(FlatMap<t, A> fm) {
            return (Future<A>) fm;
        }

        public static <A> FlatMap<t, A> inj(Future<A> f) {
            return f;
        }

        final CompletableFuture<T> futT;

        public Future(CompletableFuture<T> futT) {
            this.futT = futT;
        }

        public Future(T value) {
            this.futT = CompletableFuture.completedFuture(value);
        }

        @Override
        public <U> FlatMap<t, U> flatMap(F<T, FlatMap<t, U>> f) {
            return new Future<U>(futT.thenCompose(t -> prj(f.apply(t)).futT));
        }

        @Override
        public <U> FlatMap<t, U> map(F<T, U> f) {
            return new Future<U>(futT.thenApply(f::apply));
        }

        public static final class t {}
    }

    static final DatabaseAlgebra<Future.t, User> futureInterp = new DatabaseAlgebra<Future.t, User>() {
        final Map<Long, User> users = new HashMap<>();

        @Override
        public FlatMap<Future.t, Boolean> create(User user) {
            if (users.containsKey(user.id)) {
                return new Future<>(false);
            } else {
                users.put(user.id, user);
                return new Future<>(true);
            }
        }

        @Override
        public FlatMap<Future.t, Either<DatabaseError, User>> read(long id) {
            if (!users.containsKey(id)) {
                return new Future<>(Either.left(new ErrorFindingUser()));
            } else {
                return new Future<>(Either.right(users.get(id)));
            }
        }

        @Override
        public FlatMap<Future.t, Either<DatabaseError, Unit>> delete(long id) {
            if (!users.containsKey(id)) {
                return new Future<>(
                        Either.left(new ErrorDeletingUser("No such user"))
                );
            } else {
                users.remove(id);
                return new Future<>(Either.right(Unit.UNIT));
            }
        }
    };

    public static class Id<T> implements FlatMap<Id.t, T> {
        public static <T> Id<T> pure(T value) {
            return new Id<T>(value);
        }

        public static <A> Id<A> prj(FlatMap<t, A> m) {
            return (Id<A>) m;
        }

        public static <A> FlatMap<t, A> inj(Id<A> id) {
            return id;
        }

        public final T value;

        public Id(T value) {
            this.value = value;
        }

        @Override
        public <U> FlatMap<t, U> flatMap(F<T, FlatMap<t, U>> f) {
            return f.apply(value);
        }

        @Override
        public <U> FlatMap<t, U> map(F<T, U> f) {
            return flatMap(t -> new Id<U>(f.apply(t)));
        }

        public static final class t {}
    }

    static final DatabaseAlgebra<Id.t, User> idInterp = new DatabaseAlgebra<Id.t, User>() {
        final Map<Long, User> users = new HashMap<>();

        @Override
        public FlatMap<Id.t, Boolean> create(User user) {
            if (users.containsKey(user.id)) {
                return new Id<>(false);
            } else {
                users.put(user.id, user);
                return new Id<>(true);
            }
        }

        @Override
        public FlatMap<Id.t, Either<DatabaseError, User>> read(long id) {
            if (!users.containsKey(id)) {
                return new Id<>(Either.left(new ErrorFindingUser()));
            } else {
                return new Id<>(Either.right(users.get(id)));
            }
        }

        @Override
        public FlatMap<Id.t, Either<DatabaseError, Unit>> delete(long id) {
            if (!users.containsKey(id)) {
                return new Id<>(
                        Either.left(new ErrorDeletingUser("No such user"))
                );
            } else {
                users.remove(id);
                return new Id<>(Either.right(Unit.UNIT));
            }
        }
    };

    static abstract class UserRepo<M> {

        final DatabaseAlgebra<M, User> dbAlg;

        UserRepo(DatabaseAlgebra<M, User> dbAlg) {
            this.dbAlg = dbAlg;
        }

        abstract <T> FlatMap<M, T> pure(T value);

        FlatMap<M, Boolean> addUser(User user) {
            return dbAlg.create(user);
        }

        FlatMap<M, Either<DatabaseError, User>> getUser(long id) {
            return dbAlg.read(id);
        }

        FlatMap<M, Either<DatabaseError, User>> updateUser(User user) {
            return rightMap(dbAlg.read(user.id), dbUser ->
                    rightMap(dbAlg.delete(user.id), unit ->
                            dbAlg.create(user).map(success ->
                                    success ?
                                    Either.right(dbUser) :
                                    Either.left(new ErrorUpdatingUser())
                            )
                    )
            );
        }

        <E, T, U> FlatMap<M, Either<E, U>> rightMap(
                FlatMap<M, Either<E, T>> mET,
                F<T, FlatMap<M, Either<E, U>>> next
        ) {
            return mET.flatMap(eT -> eT.fold(e -> pure(Either.left(e)), next));
        }
    }

    static <M> FlatMap<M, String> program(UserRepo<M> repo) {
        return repo.addUser(new User(23, "John"))
                .flatMap(success ->
                        repo.updateUser(new User(23, "JonH"))
                                .map(errorOrUser ->
                                        errorOrUser.fold(
                                                error -> error.toString(),
                                                oldUser -> oldUser.name
                                )
                        )
                );
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final UserRepo<Future.t> futRepo = new UserRepo<Future.t>(futureInterp) {
            @Override
            <T> FlatMap<Future.t, T> pure(T value) {
                return new Future<T>(value);
            }
        };

        final Future<String> futRes = Future.prj(program(futRepo));
        System.out.println("Result = " + futRes.futT.get());

        final UserRepo<Id.t> idRepo = new UserRepo<Id.t>(idInterp) {
            @Override
            <T> FlatMap<Id.t, T> pure(T value) {
                return new Id<T>(value);
            }
        };

        final Id<String> idRes = Id.prj(program(idRepo));
        System.out.println("Result = " + idRes.value);
    }
}
