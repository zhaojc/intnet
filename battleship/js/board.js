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

    ships.push(new Ship([[0, 0], [0, 1], [0, 2]]));

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


function createAreas(areas, area, rectangle) {
    if(area[0] < rectangle[0]) { //left
        areas.push([area[0], area[1], area[2], rectangle[2]]);
        console.log('left: ' + [area[0], area[1], area[2], rectangle[2]]);
    }
    
    if((area[0] + area[1]) > (rectangle[0] + rectangle[1])) { //right
        areas.push([area[0], area[1], (rectangle[2] + rectangle[3]), ((area[2] + area[3]) - rectangle[2] + rectangle[3])]);
        console.log('right: ' + [area[0], area[1], (rectangle[2] + rectangle[3]), (area[3] - rectangle[2] - rectangle[3])]);
    }
    
    if(area[2] < rectangle[2]) { //top
        areas.push([(area[0]), (rectangle[0] - area[0]), rectangle[2], rectangle[3]]);
            console.log('top: ' + [(area[0]), (rectangle[0] - area[0]), rectangle[2], rectangle[3]]);
    }
    
    if((area[2] + area[3]) > (rectangle[2] + rectangle[3])) { //bottom
        areas.push([(rectangle[0] + rectangle[1]), (area[1] - (rectangle[0] + rectangle[1])), rectangle[2], rectangle[3]]);
                console.log('bottom: ' + [(rectangle[0] + rectangle[1]), (area[1] - (rectangle[0] + rectangle[1])), rectangle[2], rectangle[3]]);
    }
}

function randomNumber(limit) {
    return Math.floor(Math.random() * limit);
}

var ships = [];

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
        areas.splice(areaIndex, 1);
        continue;
    }
    
    var rowWise = randomNumber(2) == 0;
    
    var rectangle,
        ship = [];
    if(rowWisePossible && rowWise || !columnWisePossible) {
        var startRow = area[0] + randomNumber(area[1] - size),
            column = area[2] + randomNumber(area[3] - area[2]);
        
        rectangle = [startRow, size, column, 1];
        
        for(var i = 0; i < size; i++) {
            ship.push([(startRow + i), column]);
        }
    } else {
        var row = area[0] + randomNumber(area[1] - area[0]),
            startColumn = area[2] + randomNumber(area[3] - size);
        
        rectangle = [row, 1, startColumn, size];
        
        for(var i = 0; i < size; i++) {
            ship.push([row, (startColumn + 1)]);
        }
    }
    
    areas.splice(areaIndex, 1);
    createAreas(areas, area, rectangle);
    ships.push(ship);
}

for(var s = 0; i < ships.length; s++) {
    var str = '';
    for(var p = 0; p < ships[s].length; p++) {
        str += '(' + ships[s][p][0] + ', ' + ships[s][p][1] + ') ';
    }
    console.log(str);
}