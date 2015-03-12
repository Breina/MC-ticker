package presentation.threads;

class AlbumCode {
	
	private String url, title;
	private String[] ids;
	
	public AlbumCode(String title, String[] ids) {
		this.title = title;
		this.ids = ids;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setIds(String[] ids) {
		this.ids = ids;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String[] getIds() {
		return ids;
	}
}
