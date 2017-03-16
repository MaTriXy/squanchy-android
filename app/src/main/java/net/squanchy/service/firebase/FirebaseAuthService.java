package net.squanchy.service.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.squanchy.support.lang.Func1;
import net.squanchy.support.lang.Optional;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class FirebaseAuthService {

    private final FirebaseAuth auth;

    public FirebaseAuthService(FirebaseAuth auth) {
        this.auth = auth;
    }

    public <T> Observable<T> signInAndObserve(Func1<String, Observable<T>> andThen) {
        return currentUser().flatMap(user -> {
            if (user.isPresent()) {
                return andThen.call(user.get().getUid());
            }

            return signInAnonymously().andThen(Observable.empty());
        });
    }

    public Completable signInAndComplete(Func1<String, Completable> andThen) {
        return currentUser().flatMapCompletable(user -> {
            if (user.isPresent()) {
                return andThen.call(user.get().getUid());
            }

            return signInAnonymously().andThen(Completable.never());
        });
    }

    private Observable<Optional<FirebaseUser>> currentUser() {
        return Observable.create(e -> {
            FirebaseAuth.AuthStateListener listener = firebaseAuth -> e.onNext(Optional.fromNullable(firebaseAuth.getCurrentUser()));

            auth.addAuthStateListener(listener);

            e.setCancellable(() -> auth.removeAuthStateListener(listener));
        });
    }

    private Completable signInAnonymously() {
        return Completable.create(e -> auth.signInAnonymously()
                .addOnSuccessListener(result -> e.onComplete())
                .addOnFailureListener(e::onError));
    }
}
