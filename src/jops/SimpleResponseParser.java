package jops;

public class SimpleResponseParser implements ResponseParser {

    @Override
    public Response parse(String input) {
	String[] words = input.split("\\s");

	Response r;

	if (0 == words.length) {
	    r = new FailResponse(input);
	} else {
	    switch (words[0]) {
	    case "OHAI":
		r = new OhaiResponse(input.substring(5));
		break;
	    case "STAT":
		r = new StateChangeResponse(State.valueOf(words[1]
			.toUpperCase()), State.valueOf(words[2].toUpperCase()));
		break;
	    case "TIME":
		r = new TimeResponse(Long.valueOf(words[1]).longValue());
		break;
	    default:
		r = new FailResponse(input);
		break;
	    }
	}
	return r;
    }

}
