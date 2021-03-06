$(document).ready(function(){
    var players = []
        currentTarget = 1
        currentSelection = null,
        hasFired = false;


    /*
        UTILS
    */  
    function getAttackerPlayerIndex() {
        return (currentTarget == 0) ? 1 : 0;
    }

    function getCellText(cell) {
        return '(' + (cell.parent().index() + 1) + ', ' + (cell.index() + 1) + ')';
    }

    function addCombatLogEntry(hit, target) {
        var entry = $('<p class="log-entry"></p>');
        entry.html('<b>' + players[getAttackerPlayerIndex()].getName() + '</b> fired at ' + getCellText(target) + ': <i>' + (hit ? 'HIT' : 'MISS') + '</i>!');

        $('#combat-log').prepend(entry);
    }

    function alert(type, message) {
        $('#alert-container').html('<div class="alert-box ' + type + '" data-alert>' + message + '<a href="#" class="close">&times;</a></div>');
    }
    $('#alert-container').on('click', 'a', function() {
        $(this).parent().remove();
    });

    function reset() {
        players = [];
        currentTarget = 1;
        currentSelection = null;
        hasFired = false;

        $('#input-player-id').text('1');
        $('#player-name').val('');
        $('#combat-log').empty();
    }


    /*
        PRE-GAME
    */
    $('.play-btn').click(function() {
        if(players.length !== 0) {
            if(!confirm('Do you really want to restart?')) {
                return;
            }

            reset();
        }

        $('#info-container').hide();
        $('#game-container').hide();
        $('#grid-container').empty();
        $('#post-container').hide();
        $('#pre-container').show();
    });

    $('#create-player-btn').click(function() {
        if(players.length > 2) {
            return;
        }

        var name = $('#player-name').val();

        if(name.length == 0) {
            alert('alert', 'Name missing.');
            return;
        }

        players.push(new Player(name));

        if(players.length == 2) {
            begin();
        } else {
            $('#input-player-id').text('2');
            $('#player-name').val('');
        }
    });



    /*
        GAME ON
    */
    function begin() {
        $('#pre-container').hide(500);
        $('#game-container').show();

        players[currentTarget].getBoard().render();
    }

    $('#game-container').on('click', '.cell', function() {
        if(players[currentTarget].getBoard().hasBeenFiredAt($(this))) {
            return;
        }

        if(currentSelection !== null) {
            currentSelection.removeClass('marked');
        }

        currentSelection = $(this);
        currentSelection.addClass('marked');
        $('#target-text').text(getCellText(currentSelection));

        if(!hasFired) {
            $('#fire-btn').show();
        }
    });

    $('#fire-btn').click(function() {
        if(hasFired) {
            alert('info', 'You\'ve already fired!');
            return;
        }

        if(currentSelection === null) {
            alert('info', 'You must select a target!');
            return;
        }

        hasFired = true;

        var board = players[currentTarget].getBoard(),
            hit = board.firedAt(currentSelection);

        addCombatLogEntry(hit, currentSelection);

        if(hit && !board.hasShipLeft()) {
            end();
        }

        $('#fire-btn').hide();
        $('#next-round-btn').show();
    });

    $('#next-round-btn').click(function() {
        players[currentTarget].getBoard().hide();
        $('#next-round-btn').hide();

        currentTarget = getAttackerPlayerIndex();
        hasFired = false;

        if(currentSelection !== null) {
            currentSelection.removeClass('marked');
        }
        currentSelection = null;
        $('#target-text').empty();
        $('#fire-btn').hide();
        players[currentTarget].getBoard().render();
    });



    /*
        POST-GAME
    */
    function end() {
        $('#game-container').hide(500);

        $('#winner-presentation').text(players[getAttackerPlayerIndex()].getName() + ' has won!');
        $('#post-container').show(500);

        reset();
    }
});
