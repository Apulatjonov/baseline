package uz.signal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

final class BoundedBodySubscriber implements HttpResponse.BodySubscriber<byte[]> {
  private final int limit;
  private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  private final CompletableFuture<byte[]> result = new CompletableFuture<>();
  private Flow.Subscription subscription;

  BoundedBodySubscriber(int limit) { this.limit = limit; }

  @Override public CompletionStage<byte[]> getBody() { return result; }

  @Override public void onSubscribe(Flow.Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override public void onNext(List<ByteBuffer> items) {
    for (var item : items) {
      if (item.remaining() > limit - buffer.size()) {
        subscription.cancel();
        result.completeExceptionally(new IOException("Feed exceeds size limit"));
        return;
      }
      byte[] bytes = new byte[item.remaining()];
      item.get(bytes);
      buffer.writeBytes(bytes);
    }
    subscription.request(1);
  }

  @Override public void onError(Throwable error) { result.completeExceptionally(error); }
  @Override public void onComplete() { result.complete(buffer.toByteArray()); }
}
