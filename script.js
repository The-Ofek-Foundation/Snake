var dimensions = [25, 25];
var board;
var squareWidth;
var boardWidth;
var boardui = getElemId('board');
var brush = boardui.getContext('2d');
var snakeLength, snakeHead;
var snakeDirectionFacing;
var movingInterval;
var gameSpeed = 100;
var teleportationWalls = true;
var snakeMoving = false;

function pageReady() {
	resizeBoard();
	newGame();
	setTimeout(resizeGameSettingsTable, 0);
}

function resizeBoard() {
	docWidth = getElemWidth(contentWrapper);
	docHeight = getElemHeight(contentWrapper);
	wrapperTop = contentWrapper.offsetTop;

	boardWidth = docWidth < docHeight ? docWidth:docHeight;

	setElemWidth(boardui, boardWidth);
	setElemHeight(boardui, boardWidth);
	setElemStyle(boardui, 'left', (docWidth - boardWidth) / 2 + "px")
	boardui.setAttribute('width', boardWidth);
	boardui.setAttribute('height', boardWidth);

	squareWidth = boardWidth / (dimensions[0] + 2);

	resizeGameSettingsTable();
}

function newGame() {
	board = new Array(dimensions[0]);
	for (var i = 0; i < board.length; i++) {
		board[i] = new Array(dimensions[1]);
		for (var a = 0; a < board[i].length; a++)
			board[i][a] = 0;
	}

	snakeLength = 1;
	snakeHead = [parseInt(Math.random() * board.length),
		parseInt(Math.random() * board[0].length)];
	board[snakeHead[0]][snakeHead[1]] = -1;
	placeFood();
	snakeDirectionFacing = -1;

	drawBoard();
}

function placeFood() {
	var x, y;
	do {
		x = parseInt(Math.random() * board.length);
		y = parseInt(Math.random() * board[0].length);
	}	while (board[x][y] !== 0);
	board[x][y] = 1;
}

function startMoving() {
	stopMoving();
	movingInterval = setInterval(function () {
		moveSnake(true);
	}, gameSpeed);
	snakeMoving = true;
}

function stopMoving() {
	clearInterval(movingInterval);
	snakeMoving = false;
}

function clearBoard() {
	brush.clearRect(0, 0, boardWidth, boardWidth);
	brush.fillStyle = 'white';
	brush.fillRect(0, 0, boardWidth, boardWidth);
}

function drawBorder() {
	brush.fillStyle = 'black';
	brush.fillRect(0, 0, squareWidth, boardWidth);
	brush.fillRect(0, 0, boardWidth, squareWidth);
	brush.fillRect(boardWidth - squareWidth, 0, squareWidth, boardWidth);
	brush.fillRect(0, boardWidth - squareWidth, boardWidth, squareWidth);
}

function drawSquare(x, y) {
	switch (board[x][y]) {
		case 0: return;
		case 1: brush.fillStyle = 'red';
		default:
			if (board[x][y] < 0)
				brush.fillStyle = getSnakeStyle(board[x][y]);
			break;
	}
	x++; y++;
	brush.fillRect(x * squareWidth, y * squareWidth, squareWidth, squareWidth);
}

function getSnakeStyle(num) {
	switch (Math.ceil(Math.sqrt(-num))) {
		case 1: return '#2e2e2e';
		case 2: return '#3d3d3d';
		case 3: return '#4c4c4c';
		case 4: return '#5c5c5c';
		case 5: return '#6b6b6b';
		case 6: return '#7a7a7a';
		case 7: return '#8a8a8a';
		case 8: return '#999999';
		case 9: return '#a9a9a9';
		case 10: return '#b0b0b0';
		case 11: return '#b8b8b8';
		case 12: return '#c0c0c0';
		case 13: return '#c8c8c8';
		case 14: return '#d0d0d0';
		case 15: return '#d7d7d7';
		case 16: return '#dfdfdf';
		case 17: return '#e7e7e7';
		case 18: return '#efefef';
		case 19: return '#f7f7f7';
		case 20: return '#ffffff';
	}
}

function drawBoard() {
	clearBoard();
	drawBorder();

	for (var i = 0; i < board.length; i++)
		for (var a = 0; a < board[i].length; a++)
			drawSquare(i, a);
}

function getNextLocation(currentLocation, directionFacing) {
	var nextLocation = [currentLocation[0], currentLocation[1]];
	switch (directionFacing) {
		case 0: // left
			nextLocation[0]--;
			break;
		case 1: // up
			nextLocation[1]--;
			break;
		case 2: // right
			nextLocation[0]++;
			break;
		case 3: // down
			nextLocation[1]++
			break;
	}
	if (teleportationWalls) {
		nextLocation[0] = (nextLocation[0] + dimensions[0]) % dimensions[0];
		nextLocation[1] = (nextLocation[1] + dimensions[1]) % dimensions[1];
	}
	return nextLocation;
}

function decayTail() {
	for (var i = 0; i < board.length; i++)
		for (var a = 0; a < board.length; a++)
			if (board[i][a] < 0)
				if (board[i][a] === -snakeLength)
					board[i][a] = 0;
				else board[i][a]--;
}

function moveSnake(draw) {
	var tempHead = getNextLocation(snakeHead, snakeDirectionFacing);
	switch (board[tempHead[0]][tempHead[1]]) {
		case 0:
			decayTail();
			break;
		case 1:
			snakeLength++;
			decayTail();
			placeFood();
			break;
		default:
			stopMoving();
			return;
	}
	snakeHead = tempHead;
	board[snakeHead[0]][snakeHead[1]] = -1;
	if (draw)
		drawBoard();
}

document.addEventListener('keydown', function (event) {
	switch (event.which) {
		case 37: case 38: case 39: case 40:
			snakeDirectionFacing = event.which - 37;
			if (!snakeMoving)
				startMoving();
	}
});
