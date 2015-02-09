function Player(_name) {
	var name = _name,
		board = new Board();

	this.getName = function() {
		return name;
	}

	this.getBoard = function() {
		return board;
	};
}