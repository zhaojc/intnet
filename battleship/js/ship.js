function Ship(positions) {
    var positionStatus = {},
        partsLeft = positions.length;

    for(var i = 0; i < positions.length; i++) {
        positionStatus[positions[i]] = true;
    }

    this.hit = function(target) {
        var position = [target.parent().index(), target.index()];

        if(positionStatus[position] === true) {
            positionStatus[position] = false;
            target.addClass('ship');

            partsLeft--;
            return true;
        }

        return false;
    };

    this.hasSunk = function() {
        return partsLeft == 0;
    }
}
