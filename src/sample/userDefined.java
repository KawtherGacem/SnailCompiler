package sample;

public class userDefined {
    public String ident ="([a-zA-Z])+(_?([a-zA-Z0-9])+)*";
    public String Integer ="[0-9]+" ;
    public String reel ="[0-9]+.[0-9]+" ;
    public String comment ="\\w";
    public String message ="\"([a-zA-Z])+('?([a-zA-Z0-9])+)*\"";

    public String getIdent() {
        return ident;
    }

    public String getInteger() {
        return Integer;
    }

    public String getReel() {
        return reel;
    }

    public String getComment() {
        return comment;
    }

    public String getMessage() {
        return message;
    }
}
