const socket = new SockJS("/tracker");
const stompClient = Stomp.over(socket);
stompClient.connect({}, (frame) => {
	stompClient.subscribe("/topic/tracker", (data) => {
		// TODO
		console.log(data);
		addRow();
	});
});

const createRow = () => {
	// TODO
	const gridRow = document.createElement("div");
	gridRow.className = "grid-row";

	gridRow.addEventListener("click", function () {
		if (confirm("Премахни този запис?"))
			this.parentElement.removeChild(this);
	});

	const testDate = new Date();
	const seconds = testDate.getSeconds();
	const col1 = document.createElement("div");
	col1.textContent = seconds;

	const col2 = document.createElement("div");
	col2.textContent = seconds;

	const col3 = document.createElement("div");
	col3.textContent = seconds;

	const col4 = document.createElement("div");
	col4.textContent = seconds;

	gridRow.appendChild(col1);
	gridRow.appendChild(col2);
	gridRow.appendChild(col3);
	gridRow.appendChild(col4);

	return gridRow;
};

const addRow = () => {
	const table = document.querySelector(".grid-table");
	const newRow = createRow();
	table.appendChild(newRow);
};

const init = () => {
	document.getElementById("testbtn").addEventListener("click", () => {addRow()});
};

window.addEventListener("load", init);
