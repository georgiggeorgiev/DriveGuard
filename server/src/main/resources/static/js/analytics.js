const baseURL = "http://localhost:8080/"

const locationClustersURL = `${baseURL}locationClusters`;
const driverFocusLossByHourURL = `${baseURL}driverFocusLossByHour`;

const bulgariaCenterLatLng = [42.7339, 25.4858];
var map;
const accessToken = "NO-COMMIT";
var clusters = [];

const requestOptions = {
	method: "GET",
	redirect: "follow",
};

const requestUpdate = () => {
	requestLocationClustersUpdate();
	requestDriverFocusLossByHourUpdate();
};

const requestLocationClustersUpdate = async () => {
	const response = await fetch(locationClustersURL, requestOptions);
	const responseJSON = await response.text();
	onLocationClustersUpdate(responseJSON);
};

const requestDriverFocusLossByHourUpdate = async () => {
	const response = await fetch(driverFocusLossByHourURL, requestOptions);
	const responseJSON = await response.text();
	onDriverFocusLossByHourUpdate(responseJSON);
};

const fillTable = (data) => {
	const table = document.querySelector(".grid-table");
	table.innerHTML = ""; // clear table

	for (const hour in data) {
		const gridRow = document.createElement("div");
		gridRow.className = "grid-row";

		const hourCol = document.createElement("div");
		hourCol.textContent = hour;

		const countCol = document.createElement("div");
		countCol.textContent = data[hour];

		gridRow.appendChild(hourCol);
		gridRow.appendChild(countCol);

		table.appendChild(gridRow);
	}
};

const onLocationClustersUpdate = (data) => {
	// clear map
	for (const c of clusters) {
		map.removeLayer(c);
	}
	clusters = [];

	const dataObject = JSON.parse(data);
	for (const pointsObject of dataObject) {
		const cluster = L.markerClusterGroup();
		for (const pointObject of pointsObject["points"]) {
			const latlng = pointObject["point"];
			cluster.addLayer(L.marker(latlng));
		}
		clusters.push(cluster);
		map.addLayer(cluster);
	}
};

const onDriverFocusLossByHourUpdate = (data) => {
	const dataObject = JSON.parse(data);
	fillTable(dataObject);
};

const initMap = () => {
	map = L.map("map").setView(bulgariaCenterLatLng, 6);

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
	requestUpdate();
};

window.addEventListener("load", init);
