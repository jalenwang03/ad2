package ad2;


public class testPoint {

  public static void main(String[] args)  {
   int[] a={0,1,3,5,7,9,11,13,15};
   for(int i=0;i<a.length;i++){
	   int x=a[i];
	   for(int j=0;j<a.length;j++){
		   int y=a[j];
		   for(int h=0;h<a.length;h++){
			   int z=a[h];
			   System.out.println(x+" "+y+" "+z);
			   if(x+y+z==30)
				   System.out.println(x+"+"+y+"+"+z+"=30");
		   }
	   }
   }
  }
}