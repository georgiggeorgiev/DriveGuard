const socket = new SockJS("/tracker");
const stompClient = Stomp.over(socket);
stompClient.connect({}, (frame) => {
	stompClient.subscribe("/topic/tracker", onDataUpdate);
});

const locale = "bg-bg";
const bulgariaCenterLatLng = [42.7339, 25.4858];
var defaultRowExists = true;
var map;
const accessToken = "NO-COMMIT";
const markers = new Map();
var rowId = -1;

const deleteRow = (element) => {
	const marker = markers.get(element);
	if (marker) {
		map.removeLayer(marker);
	}
	markers.delete(element);
	element.remove();
};

const createRow = (rowData) => {
	const gridRow = document.createElement("div");
	gridRow.className = "grid-row";

	const deleteCol = document.createElement("div");
	deleteCol.className = "grid-row-delete-col";
	const deleteButton = document.createElement("button");
	deleteButton.textContent = "\u2421";
	deleteButton.addEventListener("click", (e) => {
		e.stopPropagation();
		deleteRow(gridRow);
	});
	deleteCol.appendChild(deleteButton);

	const timeCol = document.createElement("div");
	timeCol.textContent = new Date(rowData["localDateTime"]).toLocaleString(locale);

	const firstNameCol = document.createElement("div");
	firstNameCol.textContent = rowData["userFirstName"];

	const addressCol = document.createElement("div");
	addressCol.textContent = rowData["address"] === "" ? "[няма данни]" : rowData["address"];

	const phoneNumberCol = document.createElement("div");
	phoneNumberCol.textContent = rowData["userPhoneNumber"];

	gridRow.appendChild(deleteCol);
	gridRow.appendChild(timeCol);
	gridRow.appendChild(firstNameCol);
	gridRow.appendChild(addressCol);
	gridRow.appendChild(phoneNumberCol);

	gridRow.addEventListener("click", function() {
		const marker = markers.get(this);
		if (marker) {
			marker.openPopup();
			map.panTo(marker.getLatLng());
		}
	});

	return gridRow;
};

const deleteDefaultRow = (table) => {
	const row = document.getElementById("default-row");
	if (row) {
		table.removeChild(row);
		defaultRowExists = false;
	}
};

const addRow = (rowData) => {
	const table = document.querySelector(".grid-table");
	if (defaultRowExists)
		deleteDefaultRow(table);
	const newRow = createRow(rowData);
	table.appendChild(newRow);

	return newRow;
};

const addMapMarker = (data) => {
	const latitude = data["latitude"];
	const longitude = data["longitude"];
	const firstName = data["userFirstName"];
	const phoneNumber = data["userPhoneNumber"];
	const marker = L.marker([latitude, longitude]).addTo(map);
	marker.bindPopup(`${firstName}, ${phoneNumber}`);

	return marker;
};

const onDataUpdate = (data) => {
	const dataObject = JSON.parse(data.body);
	const row = addRow(dataObject);
	const marker = addMapMarker(dataObject);
	markers.set(row, marker);
};

const initMap = () => {
	map = L.map("map").setView(bulgariaCenterLatLng, 7);

	L.tileLayer("https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}", {
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		maxZoom: 15,
		id: "mapbox/streets-v11",
		tileSize: 512,
		zoomOffset: -1,
		accessToken: accessToken
	}).addTo(map);
};

const init = () => {
	initMap();
};

window.addEventListener("load", init);
