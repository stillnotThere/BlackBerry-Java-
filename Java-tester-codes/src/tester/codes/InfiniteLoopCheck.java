package tester.codes;

public class InfiniteLoopCheck {

	public InfiniteLoopCheck()
	{
		
	}
	
	public void NonEndingLoop()
	{
		int i = 0;
		while(true)
		{
			System.out.println(i);
			i++;
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		
	}
	
	
}
