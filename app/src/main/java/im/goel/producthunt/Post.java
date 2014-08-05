package im.goel.producthunt;

public class Post {

    private String title;
    private String url;
    private int votes;

    public Post(String title, String url, int votes) {
        this.title = title;
        this.url = url;
        this.votes = votes;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getVotes() {
        return votes;
    }
}
