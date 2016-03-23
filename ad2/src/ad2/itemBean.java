package ad2;

public class itemBean {
	private int item_ID=0;
	private String name="";
	private String catagory="";
	private String description="";
	private double latitude=0;
	private double longitude=0;
	private double buy_price=0;
	private double distance=0;
	public void setItem_ID(int item_ID){
		this.item_ID=item_ID;
	}
	public int getItem_ID(){
		return this.item_ID;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	public void setCatagory(String catagory){
		this.catagory=catagory;
	}
	public String getCatagory(){
		return this.catagory;
	}
	public void setDescription(String description){
		this.description=description;
	}
	public String getDescription(){
		return this.description;
	}
	public void setLatitude(double latitude){
		this.latitude=latitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	public void setLongitude(double longitude){
		this.longitude=longitude;
	}
	public double getLongitude(){
		return this.longitude;
	}
	public void setBuy_price(double buy_price){
		this.buy_price=buy_price;
	}
	public double getBuy_price(){
		return this.buy_price;
	}
	public void setDistance(double dist){
		this.distance=dist;
	}
	public double getDistance(){
		return this.distance;
	}

}
