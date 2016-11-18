var dimensions;
var board;
var squareWidth;
var boardWidth;
var boardui = getElemId('board');
var brush = boardui.getContext('2d');
var snakeLength, snakeHead;
var snakeDirectionFacing, lastDirectionMoved;
var movingInterval;
var gameSpeed;
var teleportationWalls;
var snakeMoving = false;
var over;

function pageReady() {
	resizeBoard();
	newGame();
	setTimeout(resizeGameSettingsTable, 0);
}

function onResize() {
	resizeBoard();
	drawBoard();
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

	resizeGameSettingsTable();
}

function newGame() {
	getSettings();
	populateSettingsForm(gameSettings.getSettings());

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
	placeItem(1);
	snakeDirectionFacing = lastDirectionMoved = -1;
	over = false;

	drawBoard();
}

function getSettings() {
	dimensions = gameSettings.getOrSet('dimensions', [25, 25]);
	gameSpeed = gameSettings.getOrSet('gameSpeed', 75);
	teleportationWalls = gameSettings.getOrSet('teleportationWalls', false);
}

function placeItem(item) {
	var x, y;
	do {
		x = parseInt(Math.random() * board.length);
		y = parseInt(Math.random() * board[0].length);
	}	while (board[x][y] !== 0);
	board[x][y] = item;
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
		case 1:
			brush.fillStyle = 'red';
			break;
		case 2:
			brush.fillStyle = 'black';
			break;
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
	squareWidth = boardWidth / (dimensions[0] + 2);
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

function killSnake() {
	stopMoving();
	over = true;
	alert("Game Over! Final Length: " + snakeLength);
}

function moveSnake(draw) {
	var tempHead = getNextLocation(snakeHead, snakeDirectionFacing);
	if (tempHead[0] === -1 || tempHead[0] === dimensions[0] ||
		tempHead[1] === -1 || tempHead[1] === dimensions[1]) {
		killSnake();
		return;
	} else switch (board[tempHead[0]][tempHead[1]]) {
		case 0:
			decayTail();
			break;
		case 1:
			snakeLength++;
			decayTail();
			placeItem(1);
			if (snakeLength % 10 === 0)
				placeItem(2);
			break;
		default:
			killSnake();
			return;
	}
	snakeHead = tempHead;
	board[snakeHead[0]][snakeHead[1]] = -1;
	lastDirectionMoved = snakeDirectionFacing;
	if (draw)
		drawBoard();
}

document.addEventListener('keydown', function (event) {
	switch (event.which) {
		case 37: case 38: case 39: case 40:
			if (over)
				newGame();
			var tempDirection = event.which - 37;
			if ((tempDirection + lastDirectionMoved) % 2 === 1 ||
				lastDirectionMoved === -1)
				snakeDirectionFacing = tempDirection;
			if (!snakeMoving)
				startMoving();
	}
});

document.addEventListener('keypress', function (event) {
	switch (event.which) {
		case 115: case 83: // s
			showSettingsForm();
			break;
		case 110: case 78: // n
			newGame();
			break;
	}
});

getElemId('done').addEventListener('click', function (event) {
	var settings = getNewSettings();
	gameSettings.setSettings(settings);
	hideSettingsForm();
	newGame();
});

getElemId('cancel').addEventListener('click', function (event) {
	hideSettingsForm();
	populateSettingsForm(gameSettings.getSettings());
});

if (getElemId('save'))
	getElemId('save').addEventListener('click', function (event) {
		var settings = getNewSettings();
		gameSettings.setSettings(settings);
		gameSettings.saveSettings(settings);
		hideSettingsForm();
		newGame();
	});

function getNewSettings() {
	return {
		'dimensions': [getInputValue('dimension-x'), getInputValue('dimension-x')],
		'gameSpeed': getInputValue('game-speed'),
		'teleportationWalls': getInputValue('teleportation-walls'),
	}
}

function populateSettingsForm(settings) {
	setInputValue('dimension-x', settings.dimensions[0]);
	setInputValue('game-speed', settings.gameSpeed);
	setInputValue('teleportation-walls', settings.teleportationWalls);
}
