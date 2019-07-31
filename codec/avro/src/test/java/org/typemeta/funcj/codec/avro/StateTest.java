package org.typemeta.funcj.codec.avro;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.System.out;

public class StateTest {
    enum State implements IState {
        READY {
            @Override public Optional<IState> start() {return Optional.of(RUNNING);}
        },
        RUNNING {
            @Override public Optional<IState> pause() {return Optional.of(PAUSED);}
            @Override public Optional<IState> finish() {return Optional.of(FINISHED);}
            @Override public Optional<IState> fail() {return Optional.of(FAILED);}
        },
        PAUSED {
            @Override public Optional<IState> resume() {return Optional.of(RUNNING);}
        },
        FAILED {
            @Override public Optional<IState> reset() {return Optional.of(READY);}
        },
        FINISHED
    }

    interface IState {
        default Optional<IState> start() {return Optional.empty();}
        default Optional<IState> pause() {return Optional.empty();}
        default Optional<IState> resume() {return Optional.empty();}
        default Optional<IState> finish() {return Optional.empty();}
        default Optional<IState> fail() {return Optional.empty();}
        default Optional<IState> reset() {return Optional.empty();}
    }

    interface Trans extends Function<IState, Optional<IState>> {}

    static public void main(String[] args) {
        out.println(run(IState::start, IState::pause, IState::resume, IState::finish));
        out.println(run(IState::start, IState::fail, IState::reset, IState::start, IState::finish));
        out.println(run(IState::start, IState::pause, IState::resume, IState::reset));
    }

    static Optional<IState> run(Trans... seq) {
        return compile(seq).apply(State.READY);
    }

    static Trans compile(Trans... seq) {
        return initState -> {
            Optional<IState> state = Optional.of(initState);
            for (Trans trans : seq) {
                state = state.flatMap(trans);
            }
            return state;
        };
    }
}
