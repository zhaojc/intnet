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

    generateShips();

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

    function generateShips() {
        var sizes = [5, 4, 3, 2];
            areas = [[0, 9, 0, 9]];

        for(var s = 0; s < sizes.length; s++) {
            var size = sizes[s];
            var areaIndex = randomNumber(areas.length),
                area = areas[areaIndex];
           
            var rowWisePossible = area[1] >= size,
                columnWisePossible = area[3] >= size;
            
            if(!rowWisePossible && !columnWisePossible) {
                s--;
                continue;
            }
            
            var rowWise = randomNumber(2) == 0;
            var rectangle,
                ship = [];
            if(rowWisePossible && rowWise || !columnWisePossible) {
                var startRow = area[0] + randomNumber(area[1] - size),
                    column = area[2] + randomNumber(area[3]);
                
                rectangle = [startRow, size, column, 1];
                
                for(var i = 0; i < size; i++) {
                    ship.push([(startRow + i), column]);
                }
            } else {
                var row = area[0] + randomNumber(area[1]),
                    startColumn = area[2] + randomNumber(area[3] - size);
                
                rectangle = [row, 1, startColumn, size];
                
                for(var i = 0; i < size; i++) {
                    ship.push([row, (startColumn + i)]);
                }
            }
            
            areas.splice(areaIndex, 1);
            createAreas(areas, area, rectangle);
            ships.push(new Ship(ship));
        }
    }

    function createAreas(areas, area, rectangle) {
        
        if(area[2] < rectangle[2]) { //left
            var a = [area[0], area[1], area[2], (rectangle[2] - area[2])];
            areas.push(a);
        }
        
        if((area[2] + area[3]) > (rectangle[2] + rectangle[3])) { //right
            var a = [area[0], area[1], (rectangle[2] + rectangle[3]), (area[2] + area[3] - (rectangle[2] + rectangle[3]))];
            areas.push(a);
        }
        
        if(area[0] < rectangle[0]) { //top
            var a = [area[0], (rectangle[0] - area[0]), rectangle[2], rectangle[3]];
            areas.push(a);
        }
        
        if((area[0] + area[1]) > (rectangle[0] + rectangle[1])) { //bottom
            var a = [(rectangle[0] + rectangle[1]), (area[0] + area[1] - (rectangle[0] + rectangle[1])), rectangle[2], rectangle[3]];
            areas.push(a);
        }
    }
}



