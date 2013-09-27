package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class SignalTest {
	private static class Signal0Spy implements Signal0.Handler {
		public int count = 0;

		@Override
		public void handle() {
			++count;
		}
	}

	@Test
	public void testConnectBasic() {
		Set<Object> tracker = new HashSet<Object>();
		Signal0 signal = new Signal0();
		Signal0Spy spy = new Signal0Spy();

		signal.connect(tracker, spy);
		signal.emit();
		assertEquals(spy.count, 1);
	}

	@Test
	public void testConnectMultiple() {
		Set<Object> tracker = new HashSet<Object>();
		Signal0 signal = new Signal0();
		Signal0Spy[] spies = new Signal0Spy[4];

		for(int x=0; x < 4; ++x) {
			Signal0Spy spy = new Signal0Spy();
			spies[x] = spy;
			signal.connect(tracker, spy);
		}
		signal.emit();
		for(Signal0Spy spy: spies) {
			assertEquals(spy.count, 1);
		}
	}

	@Test
	public void testDisconnect() {
		Set<Object> tracker = new HashSet<Object>();
		Signal0 signal = new Signal0();
		Signal0Spy spy = new Signal0Spy();

		signal.connect(tracker, spy);
		signal.emit();
		assertEquals(spy.count, 1);

		signal.disconnect(spy);
		signal.emit();

		assertEquals(spy.count, 1);
	}

	@Test
	public void testHandlerConnectWithinConnect() {
		class Handler implements Signal0.Handler {
			public Handler(Signal0 signal) {
				mSignal = signal;
			}

			@Override
			public void handle() {
				if (mSignal.getHandlerCount() == 1) {
					mSignal.connect(mTracker, mSpy);
				}
			}

			private Set<Object> mTracker = new HashSet<Object>();
			public Signal0Spy mSpy = new Signal0Spy();
			private Signal0 mSignal;
		}

		Set<Object> tracker = new HashSet<Object>();
		Signal0 signal = new Signal0();
		Handler handler = new Handler(signal);
		signal.connect(tracker, handler);

		assertEquals(signal.getHandlerCount(), 1);
		assertEquals(handler.mSpy.count, 0);

		// Emit signal, handler.mSpy should get connected, but not called yet
		signal.emit();
		assertEquals(signal.getHandlerCount(), 2);
		assertEquals(handler.mSpy.count, 0);

		// Emit signal a second time, mSpy should get called
		signal.emit();
		assertEquals(handler.mSpy.count, 1);
	}

	@Test
	public void testHandlerDisconnectWithinConnect() {
		class Handler implements Signal0.Handler {
			public Handler(Signal0 signal) {
				mSignal = signal;
			}

			@Override
			public void handle() {
				mSignal.disconnect(this);
			}

			private Signal0 mSignal;
		}

		Set<Object> tracker = new HashSet<Object>();
		Signal0 signal = new Signal0();
		Handler handler = new Handler(signal);
		signal.connect(tracker, handler);

		Signal0Spy spy = new Signal0Spy();
		signal.connect(tracker, spy);

		assertEquals(signal.getHandlerCount(), 2);
		signal.emit();
		assertEquals(signal.getHandlerCount(), 1);
	}
}
