package ffpf.git;

class Point {
	public int x,y,hash;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		// generate the hash
		hash = getHash();
	}
	public int getHash() {
		hash = 17;
        hash = ((hash + x) << 5) - (hash + x);
        hash = ((hash + y) << 5) - (hash + y);
        return hash;
	}
}