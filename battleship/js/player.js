function Player(name) {
	var name = name,
		board = new Board();

	this.getName = function() {
		return name;
	}

	this.getBoard = function() {
		return board;
	};
}