package generated;

/**
 * A barrier for two threads.
 */
public class Barrier
{
	private static final int NO_OF_THREADS = 2;

	private static Barrier theInstance;

	public static synchronized Barrier getInstance()
	{
		if (theInstance == null)
		{
			theInstance = new Barrier();
		}

		return theInstance;
	}

	private static int b=0;

	private Barrier()
	{
		b = 0;
	}

	public synchronized void check()
	{
		b++;
		if (b < NO_OF_THREADS)
		{
			try
			{
				this.wait();
			}
			catch (InterruptedException e)
			{ }
		}
		else
		{
			b = 0;

			this.notifyAll();
		}
	}
}