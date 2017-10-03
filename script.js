var dimensions;
var board, field; // determines possession
var squareWidth;
var boardWidth;
var boardui = getElemId('board');
var brush = boardui.getContext('2d');
var snakeLength, snakeHead;
var snakeLength2, snakeHead2;
var snakeDirectionFacing, lastDirectionMoved;
var snakeDirectionFacing2, lastDirectionMoved2;
var movingInterval;
var gameSpeed;
var teleportationWalls;
var snakeMoving = false;
var aiTurn;
var aiMode, aiMode2;
var over;
var aiQueue;
var aiWasHere;
var multiplayer;
var debug = false;
var boulderFrequency;
var alerts = true;

function pageReady() {
	resizeBoard();
	newGame();
	setTimeout(resizeSettingsTable, 0);
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

	resizeSettingsTable();
}

function newGame() {
	stopMoving();
	getSettings();
	populateSettingsForm(gameSettings.getSettings());

	board = new Array(dimensions[0]);
	field = new Array(dimensions[0]);
	for (var i = 0; i < board.length; i++) {
		board[i] = new Array(dimensions[1]);
		field[i] = new Array(dimensions[1]);
		for (var a = 0; a < board[i].length; a++) {
			board[i][a] = 0;
			field[i][a] = 0;
		}
	}

	aiQueue = new Array(dimensions[0] * dimensions[0] * dimensions[1]);
	aiWasHere = new Array(dimensions[0]);
	for (var i = 0; i < aiWasHere.length; i++) {
		aiWasHere[i] = new Array(dimensions[1]);
		for (var a = 0; a < aiWasHere[i].length; a++)
			aiWasHere[i][a] = new Array(dimensions[0] * dimensions[1] * 2);
	}

	snakeLength = snakeLength2 = 1;
	snakeHead = [parseInt(Math.random() * board.length),
		parseInt(Math.random() * board[0].length)];
	board[snakeHead[0]][snakeHead[1]] = -1;
	field[snakeHead[0]][snakeHead[1]] = 1;

	if (multiplayer) {
		snakeHead2 = [parseInt(Math.random() * board.length),
			parseInt(Math.random() * board[0].length)];
		board[snakeHead2[0]][snakeHead2[1]] = -1;
		field[snakeHead2[0]][snakeHead2[1]] = 2;
	}

	placeItem(1);
	snakeDirectionFacing = lastDirectionMoved = -1;
	snakeDirectionFacing2 = lastDirectionMoved2 = -1;
	over = false;

	drawBoard();
}

function getSettings() {
	dimensions = gameSettings.getOrSet('dimensions', [25, 25]);
	gameSpeed = gameSettings.getOrSet('gameSpeed', 75);
	teleportationWalls = gameSettings.getOrSet('teleportationWalls', false);
	multiplayer = gameSettings.getOrSet('multiplayer', false);
	aiTurn = gameSettings.getOrSet('aiTurn', 'none');
	aiMode = gameSettings.getOrSet('aiMode', 'shortest path');
	aiMode2 = gameSettings.getOrSet('aiMode2', 'shortest path');
	boulderFrequency = gameSettings.getOrSet('boulderFrequency', 10);
}

function placeItem(item) {
	var x, y;
	var nextLoc = getNextLocation(snakeHead, snakeDirectionFacing);
	var nextLoc2 = getNextLocation(nextLoc, snakeDirectionFacing);
	do {
		x = parseInt(Math.random() * board.length);
		y = parseInt(Math.random() * board[0].length);

		// prevents placing right in front
		if (x === nextLoc[0] && y === nextLoc[1] ||
			x === nextLoc2[0] && y === nextLoc2[1])
			continue;
	}	while (board[x][y] !== 0);
	board[x][y] = item;
}

function startMoving() {
	if (debug)
		return;
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
				brush.fillStyle = getSnakeStyle(board[x][y], field[x][y]);
			break;
	}
	x++; y++;
	brush.fillRect(x * squareWidth, y * squareWidth, squareWidth, squareWidth);
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

function decayTail(fval) {
	var sLength = fval === 1 ? snakeLength:snakeLength2;
	for (var i = 0; i < board.length; i++)
		for (var a = 0; a < board.length; a++)
			if (field[i][a] === fval)
				if (board[i][a] === -sLength)
					board[i][a] = field[i][a] = 0;
				else board[i][a]--;
}

function killSnake(fval) {
	stopMoving();
	var winval = fval === 1 ? 2:1;
	over = true;
	if (alerts)
		setTimeout(function () {
			if (multiplayer)
				alert("Game Over! Player " + winval + " won with a length of " +
					(winval === 1 ? snakeLength:snakeLength2) + "!");
			else alert("Game Over! Final Length: " + snakeLength);
		}, 10);
}

function getAiFunction(mode) {
	switch (mode) {
		case 'shortest path': return bfsAi;
		case 'shortest path fast': return shortestPathAi;
	}
}

function moveSnake(draw) {
	if (over !== false)
		return;

	if (snakeDirectionFacing !== -1)
		decayTail(1);
	if (multiplayer && snakeDirectionFacing2 !== -1)
		decayTail(2);

	if (aiTurn === 'first' || aiTurn === 'both')
		snakeDirectionFacing = getAiFunction(aiMode)(snakeHead, snakeLength);

	if (snakeDirectionFacing !== -1) {
		if (snakeLength === 1) decayTail(1);
		var tempHead = getNextLocation(snakeHead, snakeDirectionFacing);
		snakeLength = parseMove(tempHead, snakeLength, 1);
		if (over !== false)
			return;
		snakeHead = tempHead;

		board[snakeHead[0]][snakeHead[1]] = -1;
		field[snakeHead[0]][snakeHead[1]] = 1;
		lastDirectionMoved = snakeDirectionFacing;
	}

	if (aiTurn === 'second' || aiTurn === 'both')
		snakeDirectionFacing2 = getAiFunction(aiMode2)(snakeHead2, snakeLength2);

	if (multiplayer && snakeDirectionFacing2 !== -1) {
		if (snakeLength2 === 1) decayTail(2);
		tempHead = getNextLocation(snakeHead2, snakeDirectionFacing2);
		snakeLength2 = parseMove(tempHead, snakeLength2, 2);
		if (over !== false)
			return;

		snakeHead2 = tempHead;

		board[snakeHead2[0]][snakeHead2[1]] = -1;
		field[snakeHead2[0]][snakeHead2[1]] = 2;
		lastDirectionMoved2 = snakeDirectionFacing2;
	}

	if (draw && over === false)
		drawBoard();
}

function parseMove(tHead, sLength, snakeNum) {
	if (tHead[0] === -1 || tHead[0] === dimensions[0] ||
		tHead[1] === -1 || tHead[1] === dimensions[1]) {
		killSnake(snakeNum);
	} else switch (board[tHead[0]][tHead[1]]) {
		case 0: break;
		case 1:
			sLength++;
			placeItem(1);
			if (boulderFrequency !== 0 &&
				(snakeLength + snakeLength2) % boulderFrequency === 0)
				placeItem(2);
			break;
		default:
			killSnake(snakeNum);
			break;
	}
	return sLength;
}

function shortestPathAi(snakeHead, sLength) {
	var tempBoard = aiCopy(board);
	var currHead = snakeHead;
	var q = aiQueue;
	var qSize = 0, depth = 0;

	for (var i = 0; i < aiWasHere.length; i++)
		for (var a = 0; a < aiWasHere[i].length; a++)
			for (var b = 0; b < aiWasHere[i][a].length; b++)
				aiWasHere[i][a][b] = false;

	qSize = addLegalMoves(currHead, aiWasHere, q, qSize, tempBoard, depth, -1);

	var deepest = -1, deepDir = snakeDirectionFacing;

	var move = [-1, -1], tmove, lastVal, lastField; // [loc[], depth, dir]
	for (var i = 0; i < qSize; i++) {
		tmove = q[i];
		if (tempBoard[tmove[0][0]][tmove[0][1]] === 1) // food
			return tmove[2];
		if (tmove[1] === move[1]) // same depth
			tempBoard[move[0][0]][move[0][1]] = lastVal;

		move = tmove;
		depth = move[1] + 1;
		lastVal = tempBoard[move[0][0]][move[0][1]];
		tempBoard[move[0][0]][move[0][1]] = -(depth + sLength);

		qSize = addLegalMoves(move[0], aiWasHere, q, qSize, tempBoard, depth, move[2]);
		if (depth > deepest) {
			deepest = depth;
			deepDir = move[2];
		}
	}
	if (debug) {
		console.log("Game Over!", qSize, depth, q, aiWasHere);
		stopMoving();
	}
	return deepDir; // game over, but playing for the longest game possible
}

function addLegalMoves(loc, wasHere, queue, qSize, tboard, depth, dir) {
	var nextLoc, init = dir === -1, bval, tlen;
	for (var i = 0; i < 4; i++) {
		nextLoc = getNextLocation(loc, i);
		if (init)
			dir = i;
		if (nextLoc[0] === -1 || nextLoc[0] === dimensions[0] ||
			nextLoc[1] === -1 || nextLoc[1] === dimensions[1])
			continue;
		if (wasHere[nextLoc[0]][nextLoc[1]][depth])
			continue;
		bval = tboard[nextLoc[0]][nextLoc[1]];
		switch (bval) {
			case 0:
				queue[qSize] = [nextLoc, depth, dir];
				wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
				qSize++;
				break;
			case 1:
				queue[qSize] = [nextLoc, depth, dir];
				wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
				qSize++;
				return qSize;
			case 2:
				break;
			default:
				if (bval >= -depth) {
					queue[qSize] = [nextLoc, depth, dir];
					wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
					qSize++;
				}
				break;
		}
	}
	return qSize;
}

function bfsAi(snakeHead, sLength) {
	var tempBoard = aiCopy(board);
	var currHead = snakeHead;
	var q = aiQueue;
	var qSize = 0, depth = 0;

	for (var i = 0; i < aiWasHere.length; i++)
		for (var a = 0; a < aiWasHere[i].length; a++)
			for (var b = 0; b < aiWasHere[i][a].length; b++)
				aiWasHere[i][a][b] = false;

	qSize = addLegalMovesBfs(currHead, aiWasHere, q, qSize, tempBoard, depth, sLength, -1);

	var deepest = -1, deepDir = snakeDirectionFacing;

	var move = []; // [board[][], loc[], depth, dir]
	for (var i = 0; i < qSize; i++) {
		move = q[i];
		if (move[0] === 1) // food
			return move[3];

		tempBoard = move[0];
		depth = move[2] + 1;

		qSize = addLegalMovesBfs(move[1], aiWasHere, q, qSize, tempBoard, depth, sLength, move[3]);

		if (depth > deepest) {
			deepest = depth;
			deepDir = move[3];
		}
		q[i] = null;
	}
	if (debug) {
		console.log("Game Over!", qSize, depth, q, aiWasHere);
		stopMoving();
	}
	return deepDir; // game over, but playing for the longest game possible
}

function addLegalMovesBfs(loc, wasHere, queue, qSize, tboard, depth, sLength, dir) {
	var nextLoc, init = dir === -1, bval, tlen, tboard2;
	for (var i = 0; i < 4; i++) {
		nextLoc = getNextLocation(loc, i);
		if (init)
			dir = i;
		if (nextLoc[0] === -1 || nextLoc[0] === dimensions[0] ||
			nextLoc[1] === -1 || nextLoc[1] === dimensions[1])
			continue;
		if (wasHere[nextLoc[0]][nextLoc[1]][depth])
			continue;
		bval = tboard[nextLoc[0]][nextLoc[1]];
		switch (bval) {
			case 0:
				tboard2 = simpleCopy(tboard);
				tboard2[nextLoc[0]][nextLoc[1]] = -(depth + sLength);
				queue[qSize] = [tboard2, nextLoc, depth, dir];
				wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
				qSize++;
				break;
			case 1:
				queue[qSize] = [1, nextLoc, depth, dir];
				wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
				qSize++;
				return qSize;
			case 2:
				break;
			default:
				if (bval >= -depth) {
					tboard2 = simpleCopy(tboard);
					tboard2[nextLoc[0]][nextLoc[1]] = -(depth + sLength);
					queue[qSize] = [tboard2, nextLoc, depth, dir];
					wasHere[nextLoc[0]][nextLoc[1]][depth] = true;
					qSize++;
				}
				break;
		}
	}
	return qSize;
}

document.addEventListener('keydown', function (event) {
	switch (event.which) {
		case 37: case 38: case 39: case 40:
			if (over) {
				newGame();
				break;
			}
			var tempDirection = event.which - 37;
			if ((tempDirection + lastDirectionMoved) % 2 === 1 ||
				lastDirectionMoved === -1 || snakeLength <= 2)
				snakeDirectionFacing = tempDirection;
			if (!snakeMoving)
				startMoving();
			break;
		default: return;
	}
});

document.addEventListener('keypress', function (event) {
	var tempDirection;
	switch (event.which) {
		case 115: case 83: // s
			if (snakeDirectionFacing === -1 && snakeDirectionFacing2 === -1
				|| over !== false) {
				showSettingsForm();
				return;
			}
			tempDirection = 3;
			break;
		case 97: case 65: // a
			tempDirection = 0;
			break;
		case 119: case 87: // w
			tempDirection = 1;
			break;
		case 100: case 68: // d
			tempDirection = 2;
			break;
		case 110: case 78: // n
			newGame();
			return;
		case 32:
			if (debug) {
				moveSnake();
				drawBoard();
				return;
			}
			if (!snakeMoving)
				startMoving();
			return;
		default: return;
	}
	if (over) {
		newGame();
		return;
	}
	if ((tempDirection + lastDirectionMoved2) % 2 === 1 ||
		lastDirectionMoved2 === -1 || snakeLength2 <= 2)
		snakeDirectionFacing2 = tempDirection;
	if (!snakeMoving)
		startMoving();
});

function getNewSettings() {
	return {
		'dimensions': [getInputValue('dimension-x'), getInputValue('dimension-x')],
		'gameSpeed': getInputValue('game-speed'),
		'teleportationWalls': getInputValue('teleportation-walls'),
		'multiplayer': getInputValue('multiplayer'),
		'aiTurn': getInputValue('ai-turn'),
		'aiMode': getInputValue('ai-mode'),
		'aiMode2': getInputValue('ai-mode-2'),
		'boulderFrequency': getInputValue('boulder-frequency'),
	}
}

function populateSettingsForm(settings) {
	setInputValue('dimension-x', settings.dimensions[0]);
	setInputValue('game-speed', settings.gameSpeed);
	setInputValue('teleportation-walls', settings.teleportationWalls);
	setInputValue('multiplayer', settings.multiplayer);
	setInputValue('ai-turn', settings.aiTurn);
	setInputValue('ai-mode', settings.aiMode);
	setInputValue('ai-mode-2', settings.aiMode2);
	setInputValue('boulder-frequency', settings.boulderFrequency);
}

function aiCopy(board) {
	var tempBoard = new Array(board.length);
	for (var i = 0; i < tempBoard.length; i++) {
		tempBoard[i] = new Array(board[i].length);
		for (var a = 0; a < tempBoard[i].length; a++)
			if (field[i][a] === 1)
				tempBoard[i][a] = -(snakeLength + board[i][a] + 1);
			else if (field[i][a] === 2)
				tempBoard[i][a] = -(snakeLength2 + board[i][a] + 1);
			else tempBoard[i][a] = board[i][a];
	}
	return tempBoard;
}

function simpleCopy(arr) {
	var tempArr = new Array(arr.length);
	for (var i = 0; i < tempArr.length; i++) {
		tempArr[i] = new Array(arr[i].length);
		for (var a = 0; a < tempArr[i].length; a++)
			tempArr[i][a] = arr[i][a];
	}
	return tempArr;
}

function getSnakeStyle(num, fval) {
	switch (fval) {
		case 1: // bluescale
			switch (Math.ceil(Math.sqrt(-num))) {
				case 1: return '#111c3d';
				case 2: return '#172651';
				case 3: return '#1d2f66';
				case 4: return '#23397a';
				case 5: return '#29428f';
				case 6: return '#2f4ca3';
				case 7: return '#3555b8';
				case 8: return '#3b5fcc';
				case 9: return '#4169e1';
				case 10: return '#5276e3';
				case 11: return '#6384e6';
				case 12: return '#7491e9';
				case 13: return '#869feb';
				case 14: return '#97adee';
				case 15: return '#a8baf1';
				case 16: return '#b9c8f4';
				case 17: return '#cbd6f6';
				case 18: return '#dce3f9';
				default: return '#edf1fc';
			}
		case 2: // greenscale
			switch (Math.ceil(Math.sqrt(-num))) {
				case 1: return '#004522';
				case 2: return '#005c2e';
				case 3: return '#007339';
				case 4: return '#008b45';
				case 5: return '#00a250';
				case 6: return '#00b95c';
				case 7: return '#00d067';
				case 8: return '#00e773';
				case 9: return '#00ff7f';
				case 10: return '#17ff8a';
				case 11: return '#2eff96';
				case 12: return '#45ffa1';
				case 13: return '#5cffad';
				case 14: return '#73ffb9';
				case 15: return '#8bffc4';
				case 16: return '#a2ffd0';
				case 17: return '#b9ffdc';
				case 18: return '#d0ffe7';
				default: return '#e7fff3';
			}
		case 3: // grayscale
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
				default: return '#f7f7f7';
			}
		default: return 'pink';
	}
}

function gameStats(numGames) {
	var snakeLengthSum = 0, elapsedTimeSum = 0;
	for (var i = 0; i < numGames; i++) {
		newGame();
		var startTime = new Date().getTime();
		while (over === false)
			moveSnake(false);
		var elapsedTime = (new Date().getTime() - startTime);
		console.log(snakeLength, elapsedTime);
		snakeLengthSum += snakeLength;
		elapsedTimeSum += elapsedTime;
	}
	console.log("Average Length: " + snakeLengthSum / numGames);
	console.log("Average Game Duration: " + elapsedTimeSum / numGames);
}

function gameStatsR(numGamesLeft, interval, lens, moves) {
	if (interval === undefined)
		interval = 1;
	if (lens === undefined) {
		alerts = false;
		lens = new Array(numGamesLeft);
		moves = new Array(numGamesLeft);
		for (var i = 0; i < lens.length; i++)
			lens[i] = moves[i] = 0;
	}
	if (numGamesLeft === 0) {
		console.log("\n\n\nDONE!!!\n\n\n");
		analyzeStats(lens, moves);
	} else {
		newGame();
		runGameR(numGamesLeft - 1, interval, lens, moves, 0);
	}
}

function runGameR(numGamesLeft, interval, lens, moves, moveOn) {
	if (over !== false) {
		lens[numGamesLeft] = snakeLength;
		moves[numGamesLeft] = moveOn;
		analyzeStats(lens, moves);
		gameStatsR(numGamesLeft, interval, lens, moves);
	} else {
		moveSnake(true);
		setTimeout(function() {
			runGameR(numGamesLeft, interval, lens, moves, moveOn + 1);
		}, interval);
	}
}

function analyzeStats(lens, moves) {
	console.log(lens, moves);
	var maxLength = Math.max.apply(null, lens);
	if (lens[lens.lastIndexOf(0) + 1] === maxLength)
		console.log("New Max Length!", maxLength);
	var minLength = Math.min.apply(null, lens);
	if (lens[lens.lastIndexOf(0) + 1] === minLength)
		console.log("New Max Length!", minLength);

	if (lens[0] !== 0)
		console.log("Average length:", avg(lens));
}

function avg(list) {
	var sum = 0;
	for (var i = 0; i < list.length; i++)
		sum += list[i];
	return sum / list.length;
}
