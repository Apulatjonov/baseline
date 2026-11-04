package uz.signal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Flow;
import org.junit.jupiter.api.Test;

class BoundedBodySubscriberTest {
  @Test void cancelsAnOversizedBodyBeforeBufferingIt() {
    var subscription = mock(Flow.Subscription.class);
    var body = new BoundedBodySubscriber(4);
    body.onSubscribe(subscription);
    body.onNext(List.of(ByteBuffer.wrap(new byte[]{1,2,3}), ByteBuffer.wrap(new byte[]{4,5})));
    verify(subscription).cancel();
    assertThatThrownBy(() -> body.getBody().toCompletableFuture().join())
        .hasCauseInstanceOf(java.io.IOException.class);
  }

  @Test void preservesAFeedAcrossBodyChunks() {
    var body = new BoundedBodySubscriber(4);
    body.onSubscribe(mock(Flow.Subscription.class));
    body.onNext(List.of(ByteBuffer.wrap(new byte[]{1,2})));
    body.onNext(List.of(ByteBuffer.wrap(new byte[]{3,4})));
    body.onComplete();
    assertThat(body.getBody().toCompletableFuture().join()).containsExactly(1,2,3,4);
  }
}
