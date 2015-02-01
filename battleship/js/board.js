function Board() {
    var GRID_SIZE = 9;

    var ships = [];
    var grid = $('<div class="grid"></div>');

    var column = $('<div class="small-1 columns cell">');
    var row = $('<div class="row">').append(function() {
        var columns = [];
        
        for(var i = 0; i < GRID_SIZE; i++) {
            columns.push(column.clone());
        }
        
        columns[GRID_SIZE - 1].addClass('end');         
        
        return columns;
    });
    
    grid.append(function() {
        var rows = [];
        for(var i = 0; i < GRID_SIZE; i++) {
            rows.push(row.clone());
        }

        return rows;
    });

    var a = [];
    for(var r = 0; r < 9; r++) {
        a.push([]);
        for(var c = 0; c < 9; c++) {
            a[r].push(false);
        }
    }

    var nrParts = 5;
    var directions = [[-1, 0], [0, -1], [1, 0], [0, 1]];
    var takenPositions = [];
    while(nrParts > 1) {
        var directionIndex = randomNumber(directions.length - 1);
        var direction = directions[directionIndex];
        var hasChangedDirection = false;
        var shipParts = [[randomNumber(GRID_SIZE), randomNumber(GRID_SIZE - 1)]];
        for(var i = 1; i < nrParts; i++) {
            var row = shipParts[i - 1][0] + direction[0]
                column = shipParts[i - 1][1] + direction[1];

            //console.log(a[row][column] + ' ? ' + $.inArray(getCellAt(row, column), takenPositions));

            if(row < 0 || row >= GRID_SIZE || column < 0 || column >= GRID_SIZE || a[row][column]) {//$.inArray(getCellAt(row, column), takenPositions) != -1) {
                if(hasChangedDirection) {
                    break;
                }
                //console.log(shipParts);

                direction = directions[((directionIndex + directions.length/2) % directions.length)];
                hasChangedDirection = true;
                var element = shipParts[0]; 
                shipParts.splice(0, 1);
                shipParts.splice((i - 1), 0, element);
                //console.log(shipParts);
                i--;
            } else {
                shipParts.push([row, column]);
            }
        }

        if(shipParts.length === nrParts) {
            ships.push(shipParts);

            for(var i = 0; i < nrParts; i++) {
                var part = shipParts[i];
                takenPositions.push(getCellAt(part[0], part[1]));
                a[part[0]][part[1]] = true;
            }

            nrParts--;
        }
    }

    var tmp = [];
    for(var s = 0; s < ships.length; s++) {
        var str = '';
        var cells = [];
        for(var i = 0; i < ships[s].length; i++) {
            cells.push(getCellAt(ships[s][i][0], ships[s][i][1]));
            str += '(' + ships[s][i][0] + ', ' + ships[s][i][1] + ')';
        }
        console.log(str);

        tmp.push(new Ship(cells));
    }
    ships = tmp;


    function getCellAt(row, column) {
        console.log(grid);
        return grid.children().eq(row).children().eq(column);
    }


    this.firedAt = function(target) {
        target.addClass('fired-at');

        for(var i = 0; i < ships.length; i++) {
            if(ships[i].hit(target)) {
                return true;
            }
        }

        return false;
    };

    this.hasBeenFiredAt = function(target) {
        return target.hasClass('fired-at');
    }

    this.hasShipLeft = function() {
        for(var i = 0; i < ships.length; i++) {
            if(!ships[i].hasSunk()) {
                return true;
            }
        }

        return false;
    };

    this.render = function() {
        $('#grid-container').append(grid);
    };

    this.hide = function() {
        grid.remove();
    };

    function randomNumber(limit) {
        return Math.floor(Math.random() * limit);
    }
}
