package jops;

public class OhaiResponse implements Response {
    private String name;

    public OhaiResponse(String inName) {
	this.name = inName;
    }

    @Override
    public void handle(Model model) {
	// TODO Auto-generated method stub
	model.setName(this.name);
    }
}
