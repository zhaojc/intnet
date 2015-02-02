function Ship(positions) {
    this.positions = {};
    var partsLeft = positions.length;

    for(var i = 0; i < positions.length; i++) {
        console.log(positions[i]);
        positions[i].addClass('ship');
        this.positions[positions[i]] = true;
    }    

    this.hit = function(target) {
        console.log(this.positions[target]);
        console.log(this.positions[target] == true);
        console.log(this.positions[target] === true);
        if(this.positions[target] === true) {
            this.positions[target] = false;

            partsLeft--;
            return true;
        }

        return false;
    };

    this.hasSunk = function() {
        return partsLeft == 0;
    }
}
