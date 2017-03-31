package com.ocdsoft.bacta

import co.paralleluniverse.actors.Actor
import co.paralleluniverse.actors.BasicActor
import co.paralleluniverse.actors.MailboxConfig
import co.paralleluniverse.common.util.Exceptions
import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.SuspendExecution
import co.paralleluniverse.strands.Strand
import co.paralleluniverse.strands.channels.Channels
import spock.lang.Specification

import java.util.concurrent.ExecutionException

/**
 * Created by kyle on 11/27/2016.
 */
class ActorTest extends Specification {

  static final MailboxConfig mailboxConfig = new MailboxConfig(10, Channels.OverflowPolicy.THROW);


  private <Message, V> Actor<Message, V> spawnActor(Actor<Message, V> actor) {
    Fiber fiber = new Fiber("actor", scheduler, actor);
    fiber.setUncaughtExceptionHandler(new Strand.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Strand s, Throwable e) {
        e.printStackTrace();
        throw Exceptions.rethrow(e);
      }
    });
    fiber.start();
    return actor;
  }

  def "TryActors" () {

    Actor<Message, Integer> actor = spawnActor(new BasicActor<Message, Integer>(mailboxConfig) {
      @Override
      protected Integer doRun() throws SuspendExecution, InterruptedException {
        throw new RuntimeException("foo");
      }
    });

    try {
      actor.get();
      fail();
    } catch (ExecutionException e) {
      assertThat(e.getCause(), instanceOf(RuntimeException.class));
      assertThat(e.getCause().getMessage(), is("foo"));
    }


  }

  static class Message {
    final int num;

    public Message(int num) {
      this.num = num;
    }
  }


}
