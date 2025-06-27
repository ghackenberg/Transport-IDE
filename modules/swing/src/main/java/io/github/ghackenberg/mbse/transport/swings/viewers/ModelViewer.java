package io.github.ghackenberg.mbse.transport.swings.viewers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import io.github.ghackenberg.mbse.transport.core.Model;
import io.github.ghackenberg.mbse.transport.core.Statistics;
import io.github.ghackenberg.mbse.transport.core.entities.Coordinate;
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public class ModelViewer implements Viewer {
	
	private static final String FONT_NAME = "Arial";
	private static final int FONT_STYLE = Font.PLAIN;
	
	private static final double MARGIN = 10; // in pixels!
	
	private static final double LANE_WIDTH = 4; // in meters!
	private static final double VEHICLE_WIDTH = 3; // in meters!
	
	private static final Color INTERSECTION_COLOR = Color.GRAY;
	private static final Color SEGMENT_COLOR = Color.LIGHT_GRAY;
	private static final Color STATION_COLOR = Color.MAGENTA;
	private static final Color VEHICLE_DEFAULT_COLOR = Color.BLUE;
	private static final Color VEHICLE_ACCEPT_COLOR = Color.GREEN;
	private static final Color VEHICLE_DECLINE_COLOR = Color.RED;
	private static final Color VEHICLE_COLLISION_COLOR = Color.ORANGE;
	private static final Color VEHICLE_BATTERY_LEVEL_FOREGROUND_COLOR = Color.PINK;
	private static final Color VEHICLE_BATTERY_LEVEL_BACKGROUND_COLOR = Color.GRAY;
	private static final Color VEHICLE_DEMAND_LEVEL_FOREGROUND_COLOR = Color.GREEN;
	private static final Color VEHICLE_DEMAND_LEVEL_BACKGROUND_COLOR = Color.GRAY;
	private static final Color DEMAND_WAIT_COLOR = Color.LIGHT_GRAY;
	private static final Color DEMAND_RIDE_COLOR = Color.DARK_GRAY;
	private static final Color DEMAND_UNDERDUE_COLOR = Color.GREEN;
	private static final Color DEMAND_OVERDUE_COLOR = Color.RED;
	
	private final Model model;
	private final Statistics statistics;
	
	private double minLatitude = Double.MAX_VALUE;
	private double minLongitude = Double.MAX_VALUE;
	private double maxLatitude = -Double.MAX_VALUE;
	private double maxLongitude = -Double.MAX_VALUE;
	
	private double rangeLatitude;
	private double rangeLongitude;
	
	private int minLanes = Integer.MAX_VALUE;
	private int maxLanes = Integer.MIN_VALUE;
	
	private double ratioScreenModel = 1;
	
	private double paddingLeft = 0;
	private double paddingTop = 0;
	
	private JPanel panel;
	
	public ModelViewer(Model model, Statistics statistics) {
		this.model = model;
		this.statistics = statistics;
		
		model.intersections.forEach(intersection -> {
			minLatitude = Math.min(minLatitude, intersection.getCoordinate().getX());
			minLongitude = Math.min(minLongitude, intersection.getCoordinate().getY());
			maxLatitude = Math.max(maxLatitude, intersection.getCoordinate().getX());
			maxLongitude = Math.max(maxLongitude, intersection.getCoordinate().getY());
		});
		
		rangeLatitude = maxLatitude - minLatitude;
		rangeLongitude = maxLongitude - minLongitude;
		
		model.segments.forEach(segment -> {
			minLanes = (int) Math.min(minLanes, segment.lanes);
			maxLanes = (int) Math.max(maxLanes, segment.lanes);
		});
		
		initialize();
		update();
	}
	
	private void initialize() {
		
		System.out.println("Viewer.initialize");
		
		// Create label
		panel = new JPanel() {
			private static final long serialVersionUID = 5830835442493826844L;
			@Override
			public void paintComponent(Graphics graphics) {
				// Calculate size
				double width = panel.getWidth() - MARGIN * 2;
				double height = panel.getHeight() - MARGIN * 2;
				// Calculate ratios
				double ratioScreenModelWidth = width / rangeLatitude;
				double ratioScreenModelHeight = height / rangeLongitude;
				// Update ratio
				ratioScreenModel = Math.min(ratioScreenModelWidth, ratioScreenModelHeight);
				// Update padding
				paddingLeft = width * (ratioScreenModelWidth - ratioScreenModel) / ratioScreenModelWidth / 2 + maxLanes * LANE_WIDTH * ratioScreenModel / 2;
				paddingTop = height * (ratioScreenModelHeight - ratioScreenModel) / ratioScreenModelHeight / 2 + maxLanes * LANE_WIDTH * ratioScreenModel / 2;
				// Draw
				draw((Graphics2D) graphics);
			}
		};
		
	}
	
	@Override
	public String getName() {
		return "Model";
	}
	
	@Override
	public Icon getIcon() {
		try {
			URL resource = ModelViewer.class.getResource("/icons/model.jpg");
			Image image = ImageIO.read(resource);
			return new ImageIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}
	
	@Override
	public void update() {
		panel.repaint();
	}

	private void draw(Graphics2D graphics) {
		
		// System.out.println("Viewer.update");
		
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Clear image
		graphics.clearRect(0, 0, panel.getWidth(), panel.getHeight());
		
		// Fill rectangle
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		
		// Draw time
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, 10));
		
		String timeText = "Time: " + Math.round(model.time / 1000.0) + "s";
		
		int timeTextHeight = graphics.getFontMetrics().getAscent();
		
		graphics.setColor(Color.BLACK);
		graphics.drawString(timeText, 10, 10 + timeTextHeight);
		
		// Draw segments
		for (Segment segment : model.segments) {
			drawSegment(graphics, segment);
		}
		// Draw intersections
		for (Intersection intersection : model.intersections) {
			drawIntersection(graphics, intersection);
		}
		// Draw stations
		for (Station station : model.stations) {
			drawStation(graphics, station);
		}
		// Draw demands
		for (Demand demand : model.demands) {
			if (!demand.done && demand.pickup.time <= model.time) {
				drawDemand(graphics, demand);
			}
		}
		// Draw directions
		for (Vehicle vehicle : model.vehicles) {
			drawSpeed(graphics, vehicle);
		}
		// Draw battery levels
		for (Vehicle vehicle : model.vehicles) {
			drawBatteryLevel(graphics, vehicle);
		}
		// Draw demand levels
		for (Vehicle vehicle : model.vehicles) {
			drawDemandLevel(graphics, vehicle);
		}
		// Draw vehicles
		for (Vehicle vehicle : model.vehicles) {
			drawVehicle(graphics, vehicle);
		}
		
	}
	
	private void drawSegment(Graphics2D graphics, Segment segment) {
		
		double startX = calculateX(segment.start.getCoordinate());
		double startY = calculateY(segment.start.getCoordinate());
		
		double endX = calculateX(segment.end.getCoordinate());
		double endY = calculateY(segment.end.getCoordinate());
		
		double deltaX = calculateDeltaX(segment);
		double deltaY = calculateDeltaY(segment);
		
		double stepX = (Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY);
		double stepY = (Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY);
		
		double leftX = stepX * segment.lanes * LANE_WIDTH * ratioScreenModel / 2;
		double leftY = stepY * segment.lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		Color color = SEGMENT_COLOR;
		
		for (Vehicle vehicle : model.vehicles) {
			if (vehicle.location.getDistance() == vehicle.location.getSegment().getLength()) {
				if (vehicle.location.getSegment().end == segment.start) {
					color = Color.PINK;
					break;
				}
			}
		}
		
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke((int) (segment.lanes * LANE_WIDTH * ratioScreenModel), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
		
		for (int lane = 1; lane < segment.lanes; lane++) {
			double step = lane * LANE_WIDTH * ratioScreenModel;
			
			graphics.setColor(Color.WHITE);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
			graphics.drawLine((int) (startX - leftX + stepX * step), (int) (startY - leftY + stepY * step), (int) (endX - leftX + stepX * step), (int) (endY - leftY + stepY * step));
		}
		
	}
	
	private void drawIntersection(Graphics2D graphics, Intersection intersection) {
		
		double centerX = calculateX(intersection.getCoordinate());
		double centerY = calculateY(intersection.getCoordinate());
		
		int lanes = 0;
		for (Segment segment : intersection.incoming) {
			lanes = (int) Math.max(lanes, segment.lanes);
		}
		for (Segment segment : intersection.outgoing) {
			lanes = (int) Math.max(lanes, segment.lanes);
		}
		
		double radius = lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		int arcX = (int) (centerX - radius);
		int arcY = (int) (centerY - radius);
		
		graphics.setColor(INTERSECTION_COLOR);
		graphics.fillArc(arcX, arcY, (int) (radius * 2), (int) (radius * 2), 0, 360);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, (int) radius));
		
		int textWidth = graphics.getFontMetrics().stringWidth(intersection.getName());
		int textHeight = graphics.getFontMetrics().getAscent();
		
		int textX = (int) (centerX - textWidth / 2.);
		int textY = (int) (centerY + textHeight / 2.);
		
		graphics.setColor(Color.WHITE);
		graphics.drawString(intersection.getName(), textX, textY);
		
	}
	
	private void drawStation(Graphics2D graphics, Station station) {
		
		double centerX = calculateX(station.location.getCoordinate());
		double centerY = calculateY(station.location.getCoordinate());
		
		double radius = station.location.getSegment().lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		int arcX = (int) (centerX - radius);
		int arcY = (int) (centerY - radius);
		
		int arcWidth = (int) (radius * 2);
		int arcHeight = (int) (radius * 2);
		
		graphics.setColor(STATION_COLOR);
		graphics.fillArc(arcX, arcY, arcWidth, arcHeight, 0, 360);
		
	}
	
	private void drawSpeed(Graphics2D graphics, Vehicle vehicle) {
		
		double centerX = calculateCenterX(vehicle);
		double centerY = calculateCenterY(vehicle);
		
		double vehicleLength = ratioScreenModel * vehicle.length / 2;
		
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double leftX = (Math.cos(+ Math.PI / 2) * deltaX - Math.sin(+ Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		double leftY = (Math.sin(+ Math.PI / 2) * deltaX + Math.cos(+ Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		
		double rightX = (Math.cos(- Math.PI / 2) * deltaX - Math.sin(- Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		double rightY = (Math.sin(- Math.PI / 2) * deltaX + Math.cos(- Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		
		double startX = centerX + deltaX * vehicleLength;
		double startY = centerY + deltaY * vehicleLength;
		
		double endX = startX + deltaX * vehicle.speed * 1000 / 60 / 60 * ratioScreenModel;
		double endY = startY + deltaY * vehicle.speed * 1000 / 60 / 60 * ratioScreenModel;
		
		double middleX = endX - deltaX * 1 * ratioScreenModel;
		double middleY = endY - deltaY * 1 * ratioScreenModel;
		
		Polygon polygon = new Polygon();
		polygon.addPoint((int) (middleX + rightX), (int) (middleY + rightY));
		polygon.addPoint((int) (middleX + leftX), (int) (middleY + leftY));
		polygon.addPoint((int) endX, (int) endY);
		
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(1));
		graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
		
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(1));
		graphics.fillPolygon(polygon);
		
	}
	
	private void drawBatteryLevel(Graphics2D graphics, Vehicle vehicle) {
		
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle);
		double centerY = calculateCenterY(vehicle);
		
		double vehicleLength = ratioScreenModel * vehicle.length / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double factor1 = 1.1;
		double factor2 = 1.3;
		
		double percentage = vehicle.batteryLevel / vehicle.batteryCapacity;
		
		// Background
		Polygon background = new Polygon();
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) + stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor1) + stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) - stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor1) - stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) - stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor2) - stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) + stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor2) + stepY * vehicleWidth));

		graphics.setColor(VEHICLE_BATTERY_LEVEL_BACKGROUND_COLOR);
		graphics.fillPolygon(background);
		
		// Foreground
		Polygon foreground = new Polygon();
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) + stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor1) + stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) - stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor1) - stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) - stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor2) - stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) + stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor2) + stepY * vehicleWidth * percentage));

		graphics.setColor(VEHICLE_BATTERY_LEVEL_FOREGROUND_COLOR);
		graphics.fillPolygon(foreground);
		
	}
	
	private void drawDemandLevel(Graphics2D graphics, Vehicle vehicle) {
		
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle);
		double centerY = calculateCenterY(vehicle);
		
		double vehicleLength = ratioScreenModel * vehicle.length / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double factor1 = 1.4;
		double factor2 = 1.6;
		
		double percentage = vehicle.loadLevel / vehicle.loadCapacity;
		
		// Background
		Polygon background = new Polygon();
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) + stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor1) + stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) - stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor1) - stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) - stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor2) - stepY * vehicleWidth));
		background.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) + stepX * vehicleWidth), (int) (centerY - deltaY * (vehicleLength * factor2) + stepY * vehicleWidth));

		graphics.setColor(VEHICLE_DEMAND_LEVEL_BACKGROUND_COLOR);
		graphics.fillPolygon(background);
		
		// Foreground
		Polygon foreground = new Polygon();
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) + stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor1) + stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor1) - stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor1) - stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) - stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor2) - stepY * vehicleWidth * percentage));
		foreground.addPoint((int) (centerX - deltaX * (vehicleLength * factor2) + stepX * vehicleWidth * percentage), (int) (centerY - deltaY * (vehicleLength * factor2) + stepY * vehicleWidth * percentage));

		graphics.setColor(VEHICLE_DEMAND_LEVEL_FOREGROUND_COLOR);
		graphics.fillPolygon(foreground);
		
	}
	
	private void drawVehicle(Graphics2D graphics, Vehicle vehicle) {

		double vehicleFront = vehicle.location.getDistance() + vehicle.length / 2;
		double vehicleBack = vehicle.location.getDistance() - vehicle.length / 2;
		
		boolean decline = false;
		for (Entry<Demand, Map<Double, Vehicle>> entry : statistics.demandPickupDeclineTimes.entrySet()) {
			if (entry.getValue().containsKey(model.time) && entry.getValue().get(model.time) == vehicle) {
				decline = true;
				break;
			}
		}
		boolean accept = false;
		for (Entry<Demand, Map<Double, Vehicle>> entry : statistics.demandPickupAcceptTimes.entrySet()) {
			if (entry.getValue().containsKey(model.time) && entry.getValue().get(model.time) == vehicle) {
				accept = true;
				break;
			}
		}
		boolean decide = false;
		for (Demand demand : model.demands) {
			if (demand.done == false && demand.vehicle == null && demand.pickup.time <= model.time) {
				if (demand.pickup.location.getSegment() == vehicle.location.getSegment()) {
					if (demand.pickup.location.getDistance() == vehicle.location.getDistance()) {
						decide = true;
						break;
					}
				}
			}
		}
		boolean dropoff = false;
		for (Demand demand : vehicle.demands) {
			if (demand.dropoff.location.getSegment() == vehicle.location.getSegment()) {
				if (demand.dropoff.location.getDistance() == vehicle.location.getDistance()) {
					dropoff = true;
					break;
				}
			}
		}
		boolean attach = false;
		boolean detach = false;
		for (Vehicle other : model.vehicles) {
			if (other.location.getSegment() == vehicle.location.getSegment()) {
				double otherFront = other.location.getDistance() + other.length / 2;
				double otherBack = other.location.getDistance() - other.length / 2;
				if (otherBack == vehicleFront) {
					if (other.speed < vehicle.speed) {
						attach = true;
					} else {
						detach = true;
					}
				}
				if (otherFront == vehicleBack) {
					if (other.speed > vehicle.speed) {
						attach = true;
					} else {
						detach = true;
					}
				}
			}
		}
		boolean charge = false;
		for (Station station : model.stations) {
			if (statistics.chargeStartTimes.get(station).get(model.time) == vehicle) {
				charge = true;
				break;
			}
			if (statistics.chargeEndTimes.get(station).get(model.time) == vehicle) {
				charge = true;
				break;
			}
		}
		
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle);
		double centerY = calculateCenterY(vehicle);
		
		double vehicleLength = ratioScreenModel * vehicle.length / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double radius = Math.sqrt(vehicleLength * vehicleLength + vehicleWidth * vehicleWidth) * 1.1;
		
		if (vehicle.location.getDistance() == vehicle.location.getSegment().getLength() || decide || dropoff || attach || detach || charge) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (centerX - radius), (int) (centerY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		}
		
		Polygon polygon = new Polygon();
		polygon.addPoint((int) (centerX - deltaX * vehicleLength + stepX * vehicleWidth), (int) (centerY - deltaY * vehicleLength + stepY * vehicleWidth));
		polygon.addPoint((int) (centerX - deltaX * vehicleLength - stepX * vehicleWidth), (int) (centerY - deltaY * vehicleLength - stepY * vehicleWidth));
		polygon.addPoint((int) (centerX + deltaX * vehicleLength - stepX * vehicleWidth), (int) (centerY + deltaY * vehicleLength - stepY * vehicleWidth));
		polygon.addPoint((int) (centerX + deltaX * vehicleLength + stepX * vehicleWidth), (int) (centerY + deltaY * vehicleLength + stepY * vehicleWidth));
		
		graphics.setColor(accept ? VEHICLE_ACCEPT_COLOR : (decline ? VEHICLE_DECLINE_COLOR : (vehicle.collisions.size() > 1 ? VEHICLE_COLLISION_COLOR : VEHICLE_DEFAULT_COLOR)));
		graphics.fillPolygon(polygon);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, (int) Math.min(vehicleLength, vehicleWidth)));
		
		int textWidth = graphics.getFontMetrics().stringWidth(vehicle.name);
		int textHeight = graphics.getFontMetrics().getAscent();
		
		int textX = (int) (centerX - textWidth / 2.);
		int textY = (int) (centerY + textHeight / 2.);
		
		graphics.setColor(Color.WHITE);
		graphics.drawString(vehicle.name, textX, textY);
		
	}
	
	private void drawDemand(Graphics2D graphics, Demand demand) {
		
		Coordinate pickup = demand.pickup.location.getCoordinate();
		Coordinate dropoff = demand.dropoff.location.getCoordinate();
		
		double pickupCenterX = demand.vehicle == null ? calculateX(pickup) : calculateCenterX(demand.vehicle);
		double pickupCenterY = demand.vehicle == null ? calculateY(pickup) : calculateCenterY(demand.vehicle);
		
		double dropoffCenterX = calculateX(dropoff);
		double dropoffCenterY = calculateY(dropoff);
		
		graphics.setColor(demand.vehicle == null ? DEMAND_WAIT_COLOR : DEMAND_RIDE_COLOR);
		graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
		graphics.drawLine((int) pickupCenterX, (int) pickupCenterY, (int) dropoffCenterX, (int) dropoffCenterY);
		
		double radius = demand.size * ratioScreenModel;
		
		if (demand.pickup.time == model.time) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (pickupCenterX - radius * 1.5), (int) (pickupCenterY - radius * 1.5), (int) (radius * 3), (int) (radius * 3), 0, 360);
		}
		
		if (demand.dropoff.time == model.time) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (dropoffCenterX - radius * 1.5), (int) (dropoffCenterY - radius * 1.5), (int) (radius * 3), (int) (radius * 3), 0, 360);
		}
		
		if (demand.vehicle == null) {
			graphics.setColor(demand.dropoff.time < model.time ? DEMAND_OVERDUE_COLOR : DEMAND_UNDERDUE_COLOR);
			graphics.fillArc((int) (pickupCenterX - radius), (int) (pickupCenterY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		}
		
		graphics.setColor(demand.dropoff.time < model.time ? DEMAND_OVERDUE_COLOR : DEMAND_UNDERDUE_COLOR);
		graphics.fillArc((int) (dropoffCenterX - radius), (int) (dropoffCenterY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, 10));
		
		if (demand.vehicle == null) {
			String pickupText = Math.round(demand.pickup.time / 1000) + "s";
			
			int pickupTextWidth = graphics.getFontMetrics().stringWidth(pickupText);
			int pickupTextHeight = graphics.getFontMetrics().getAscent();
			
			graphics.setColor(Color.BLACK);
			graphics.drawString(pickupText, (int) (pickupCenterX - pickupTextWidth / 2.), (int) (pickupCenterY - pickupTextHeight));
		}
		
		String dropoffText = Math.round(demand.dropoff.time / 1000) + "s";
		
		int dropoffTextWidth = graphics.getFontMetrics().stringWidth(dropoffText);
		int dropoffTextHeight = graphics.getFontMetrics().getAscent();
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawString(dropoffText, (int) (dropoffCenterX - dropoffTextWidth / 2.), (int) (dropoffCenterY - dropoffTextHeight));
		
	}
	
	private double calculateLength(Segment segment) {
		double startX = calculateX(segment.start.getCoordinate());
		double startY = calculateY(segment.start.getCoordinate());
		
		double endX = calculateX(segment.end.getCoordinate());
		double endY = calculateY(segment.end.getCoordinate());
		
		double deltaX = endX - startX;
		double deltaY = endY - startY;
		
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
	
	private double calculateDeltaX(Segment segment) {
		double start = calculateX(segment.start.getCoordinate());		
		double end = calculateX(segment.end.getCoordinate());
		
		double delta = end - start;
		double length = calculateLength(segment);
		
		return delta / length;
	}
	
	private double calculateDeltaY(Segment segment) {
		double start = calculateY(segment.start.getCoordinate());		
		double end = calculateY(segment.end.getCoordinate());
		
		double delta = end - start;
		double length = calculateLength(segment);
		
		return delta / length;
	}
	
	private double calculateCenterX(Vehicle vehicle) {
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double step = (Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY);
		double left = step * vehicle.location.getSegment().lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		Coordinate coordinate = vehicle.location.getCoordinate();
		
		return (int) (calculateX(coordinate) - left + step * (vehicle.lane + 0.5) * LANE_WIDTH * ratioScreenModel);
	}
	
	private double calculateCenterY(Vehicle vehicle) {
		double deltaX = calculateDeltaX(vehicle.location.getSegment());
		double deltaY = calculateDeltaY(vehicle.location.getSegment());
		
		double step = (Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY);
		double left = step * vehicle.location.getSegment().lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		Coordinate coordinate = vehicle.location.getCoordinate();
		
		return (int) (calculateY(coordinate) - left + step * (vehicle.lane + 0.5) * LANE_WIDTH * ratioScreenModel);
	}
	
	private double calculateX(Coordinate coordinate) {
		return (coordinate.getX() - minLatitude) / rangeLatitude * calculateWidth() + paddingLeft + MARGIN;
	}
	
	private double calculateY(Coordinate coordinate) {
		return (coordinate.getY() - minLongitude) / rangeLongitude * calculateHeight() + paddingTop + MARGIN;
	}
	
	private double calculateWidth() {
		return panel.getWidth() - paddingLeft * 2 - MARGIN * 2;
	}
	
	private double calculateHeight() {
		return panel.getHeight() - paddingTop * 2 - MARGIN * 2;
	}

}
