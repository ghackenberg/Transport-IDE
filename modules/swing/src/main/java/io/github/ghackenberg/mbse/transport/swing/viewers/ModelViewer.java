package io.github.ghackenberg.mbse.transport.swing.viewers;

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
import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Intersection;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;
import io.github.ghackenberg.mbse.transport.core.structures.Vector;
import io.github.ghackenberg.mbse.transport.core.structures.Location;

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
			minLatitude = Math.min(minLatitude, intersection.coordinate.x.get());
			minLongitude = Math.min(minLongitude, intersection.coordinate.y.get());
			maxLatitude = Math.max(maxLatitude, intersection.coordinate.x.get());
			maxLongitude = Math.max(maxLongitude, intersection.coordinate.y.get());
		});
		
		rangeLatitude = maxLatitude - minLatitude;
		rangeLongitude = maxLongitude - minLongitude;
		
		model.segments.forEach(segment -> {
			minLanes = (int) Math.min(minLanes, segment.lanes.get());
			maxLanes = (int) Math.max(maxLanes, segment.lanes.get());
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
		
		String timeText = "Time: " + Math.round(model.state.get().time / 1000.0) + "s";
		
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
			if (!demand.state.get().done && demand.pick.time.get() <= model.state.get().time) {
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
		
		double startX = calculateX(segment.start.coordinate);
		double startY = calculateY(segment.start.coordinate);
		
		double endX = calculateX(segment.end.coordinate);
		double endY = calculateY(segment.end.coordinate);
		
		double deltaX = calculateDeltaX(segment);
		double deltaY = calculateDeltaY(segment);
		
		double stepX = (Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY);
		double stepY = (Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY);
		
		double leftX = stepX * segment.lanes.get() * LANE_WIDTH * ratioScreenModel / 2;
		double leftY = stepY * segment.lanes.get() * LANE_WIDTH * ratioScreenModel / 2;
		
		Color color = SEGMENT_COLOR;
		
		for (Vehicle vehicle : model.vehicles) {
			if (vehicle.state.get().distance == vehicle.state.get().segment.length.get()) {
				if (vehicle.state.get().segment.end == segment.start) {
					color = Color.PINK;
					break;
				}
			}
		}
		
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke((int) (segment.lanes.get() * LANE_WIDTH * ratioScreenModel), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
		
		for (int lane = 1; lane < segment.lanes.get(); lane++) {
			double step = lane * LANE_WIDTH * ratioScreenModel;
			
			graphics.setColor(Color.WHITE);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
			graphics.drawLine((int) (startX - leftX + stepX * step), (int) (startY - leftY + stepY * step), (int) (endX - leftX + stepX * step), (int) (endY - leftY + stepY * step));
		}
		
	}
	
	private void drawIntersection(Graphics2D graphics, Intersection intersection) {
		
		double centerX = calculateX(intersection.coordinate);
		double centerY = calculateY(intersection.coordinate);
		
		int lanes = 0;
		for (Segment segment : intersection.incoming) {
			lanes = (int) Math.max(lanes, segment.lanes.get());
		}
		for (Segment segment : intersection.outgoing) {
			lanes = (int) Math.max(lanes, segment.lanes.get());
		}
		
		double radius = lanes * LANE_WIDTH * ratioScreenModel / 2;
		
		int arcX = (int) (centerX - radius);
		int arcY = (int) (centerY - radius);
		
		graphics.setColor(INTERSECTION_COLOR);
		graphics.fillArc(arcX, arcY, (int) (radius * 2), (int) (radius * 2), 0, 360);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, (int) radius));
		
		int textWidth = graphics.getFontMetrics().stringWidth(intersection.name.get());
		int textHeight = graphics.getFontMetrics().getAscent();
		
		int textX = (int) (centerX - textWidth / 2.);
		int textY = (int) (centerY + textHeight / 2.);
		
		graphics.setColor(Color.WHITE);
		graphics.drawString(intersection.name.get(), textX, textY);
		
	}
	
	private void drawStation(Graphics2D graphics, Station station) {
		
		double centerX = calculateX(station.location.coordinate);
		double centerY = calculateY(station.location.coordinate);
		
		double radius = station.location.segment.get().lanes.get() * LANE_WIDTH * ratioScreenModel / 2;
		
		int arcX = (int) (centerX - radius);
		int arcY = (int) (centerY - radius);
		
		int arcWidth = (int) (radius * 2);
		int arcHeight = (int) (radius * 2);
		
		graphics.setColor(STATION_COLOR);
		graphics.fillArc(arcX, arcY, arcWidth, arcHeight, 0, 360);
		
	}
	
	private void drawSpeed(Graphics2D graphics, Vehicle vehicle) {
		
		final Location location = new Location(vehicle.state.get().segment, vehicle.state.get().distance, vehicle.state.get().lane); 
		
		double centerX = calculateCenterX(vehicle, location);
		double centerY = calculateCenterY(vehicle, location);
		
		double vehicleLength = ratioScreenModel * vehicle.length.get() / 2;
		
		double deltaX = calculateDeltaX(vehicle.state.get().segment);
		double deltaY = calculateDeltaY(vehicle.state.get().segment);
		
		double leftX = (Math.cos(+ Math.PI / 2) * deltaX - Math.sin(+ Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		double leftY = (Math.sin(+ Math.PI / 2) * deltaX + Math.cos(+ Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		
		double rightX = (Math.cos(- Math.PI / 2) * deltaX - Math.sin(- Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		double rightY = (Math.sin(- Math.PI / 2) * deltaX + Math.cos(- Math.PI / 2) * deltaY) * 1 * ratioScreenModel;
		
		double startX = centerX + deltaX * vehicleLength;
		double startY = centerY + deltaY * vehicleLength;
		
		double endX = startX + deltaX * vehicle.state.get().speed * 1000 / 60 / 60 * ratioScreenModel;
		double endY = startY + deltaY * vehicle.state.get().speed * 1000 / 60 / 60 * ratioScreenModel;
		
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
		
		final Location location = new Location(vehicle.state.get().segment, vehicle.state.get().distance, vehicle.state.get().lane); 
		
		double deltaX = calculateDeltaX(vehicle.state.get().segment);
		double deltaY = calculateDeltaY(vehicle.state.get().segment);
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle, location);
		double centerY = calculateCenterY(vehicle, location);
		
		double vehicleLength = ratioScreenModel * vehicle.length.get() / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double factor1 = 1.1;
		double factor2 = 1.3;
		
		double percentage = vehicle.state.get().batteryLevel / vehicle.batteryCapacity.get();
		
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
		
		final Location location = new Location(vehicle.state.get().segment, vehicle.state.get().distance, vehicle.state.get().lane); 
		
		double deltaX = calculateDeltaX(vehicle.state.get().segment);
		double deltaY = calculateDeltaY(vehicle.state.get().segment);
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle, location);
		double centerY = calculateCenterY(vehicle, location);
		
		double vehicleLength = ratioScreenModel * vehicle.length.get() / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double factor1 = 1.4;
		double factor2 = 1.6;
		
		double percentage = vehicle.state.get().loadLevel / vehicle.loadCapacity.get();
		
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
		
		final Location location = new Location(vehicle.state.get().segment, vehicle.state.get().distance, vehicle.state.get().lane); 

		double vehicleFront = vehicle.state.get().distance + vehicle.length.get() / 2;
		double vehicleBack = vehicle.state.get().distance - vehicle.length.get() / 2;
		
		boolean decline = false;
		for (Entry<Demand, Map<Double, Vehicle>> entry : statistics.demandPickDeclineTimes.entrySet()) {
			if (entry.getValue().containsKey(model.state.get().time) && entry.getValue().get(model.state.get().time) == vehicle) {
				decline = true;
				break;
			}
		}
		boolean accept = false;
		for (Entry<Demand, Map<Double, Vehicle>> entry : statistics.demandPickAcceptTimes.entrySet()) {
			if (entry.getValue().containsKey(model.state.get().time) && entry.getValue().get(model.state.get().time) == vehicle) {
				accept = true;
				break;
			}
		}
		boolean decide = false;
		for (Demand demand : model.demands) {
			if (demand.state.get().done == false && demand.state.get().vehicle == null && demand.pick.time.get() <= model.state.get().time) {
				if (demand.state.get().segment == vehicle.state.get().segment) {
					if (demand.state.get().distance == vehicle.state.get().distance) {
						decide = true;
						break;
					}
				}
			}
		}
		boolean dropoff = false;
		for (Demand demand : vehicle.state.get().demands) {
			if (demand.drop.location.segment.get() == vehicle.state.get().segment) {
				if (demand.drop.location.distance.get() == vehicle.state.get().distance) {
					dropoff = true;
					break;
				}
			}
		}
		boolean attach = false;
		boolean detach = false;
		for (Vehicle other : model.vehicles) {
			if (other.state.get().segment == vehicle.state.get().segment) {
				double otherFront = other.state.get().distance + other.length.get() / 2;
				double otherBack = other.state.get().distance - other.length.get() / 2;
				if (otherBack == vehicleFront) {
					if (other.state.get().speed < vehicle.state.get().speed) {
						attach = true;
					} else {
						detach = true;
					}
				}
				if (otherFront == vehicleBack) {
					if (other.state.get().speed > vehicle.state.get().speed) {
						attach = true;
					} else {
						detach = true;
					}
				}
			}
		}
		boolean charge = false;
		for (Station station : model.stations) {
			if (statistics.stationVehicleChargeStartTimes.get(station).get(model.state.get().time) == vehicle) {
				charge = true;
				break;
			}
			if (statistics.stationVehicleCargeEndTimes.get(station).get(model.state.get().time) == vehicle) {
				charge = true;
				break;
			}
		}
		
		double deltaX = calculateDeltaX(vehicle.state.get().segment);
		double deltaY = calculateDeltaY(vehicle.state.get().segment);
		
		double stepX = Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY;
		double stepY = Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY;
		
		double centerX = calculateCenterX(vehicle, location);
		double centerY = calculateCenterY(vehicle, location);
		
		double vehicleLength = ratioScreenModel * vehicle.length.get() / 2;
		double vehicleWidth =  ratioScreenModel * VEHICLE_WIDTH / 2;
		
		double radius = Math.sqrt(vehicleLength * vehicleLength + vehicleWidth * vehicleWidth) * 1.1;
		
		if (vehicle.state.get().distance == vehicle.state.get().segment.length.get() || decide || dropoff || attach || detach || charge) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (centerX - radius), (int) (centerY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		}
		
		Polygon polygon = new Polygon();
		polygon.addPoint((int) (centerX - deltaX * vehicleLength + stepX * vehicleWidth), (int) (centerY - deltaY * vehicleLength + stepY * vehicleWidth));
		polygon.addPoint((int) (centerX - deltaX * vehicleLength - stepX * vehicleWidth), (int) (centerY - deltaY * vehicleLength - stepY * vehicleWidth));
		polygon.addPoint((int) (centerX + deltaX * vehicleLength - stepX * vehicleWidth), (int) (centerY + deltaY * vehicleLength - stepY * vehicleWidth));
		polygon.addPoint((int) (centerX + deltaX * vehicleLength + stepX * vehicleWidth), (int) (centerY + deltaY * vehicleLength + stepY * vehicleWidth));
		
		graphics.setColor(accept ? VEHICLE_ACCEPT_COLOR : (decline ? VEHICLE_DECLINE_COLOR : (vehicle.state.get().collisions.size() > 1 ? VEHICLE_COLLISION_COLOR : VEHICLE_DEFAULT_COLOR)));
		graphics.fillPolygon(polygon);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, (int) Math.min(vehicleLength, vehicleWidth)));
		
		int textWidth = graphics.getFontMetrics().stringWidth(vehicle.name.get());
		int textHeight = graphics.getFontMetrics().getAscent();
		
		int textX = (int) (centerX - textWidth / 2.);
		int textY = (int) (centerY + textHeight / 2.);
		
		graphics.setColor(Color.WHITE);
		graphics.drawString(vehicle.name.get(), textX, textY);
		
	}
	
	private void drawDemand(Graphics2D graphics, Demand demand) {
		
		final Location location = new Location(demand.state.get().segment, demand.state.get().distance, demand.state.get().lane); 
		
		Vector pickup = location.coordinate;
		Vector dropoff = demand.drop.location.coordinate;
		
		double pickupCenterX = demand.state.get().vehicle == null ? calculateX(pickup) : calculateCenterX(demand.state.get().vehicle, location);
		double pickupCenterY = demand.state.get().vehicle == null ? calculateY(pickup) : calculateCenterY(demand.state.get().vehicle, location);
		
		double dropoffCenterX = calculateX(dropoff);
		double dropoffCenterY = calculateY(dropoff);
		
		graphics.setColor(demand.state.get().vehicle == null ? DEMAND_WAIT_COLOR : DEMAND_RIDE_COLOR);
		graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
		graphics.drawLine((int) pickupCenterX, (int) pickupCenterY, (int) dropoffCenterX, (int) dropoffCenterY);
		
		double radius = demand.size.get() * ratioScreenModel;
		
		if (demand.pick.time.get() == model.state.get().time) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (pickupCenterX - radius * 1.5), (int) (pickupCenterY - radius * 1.5), (int) (radius * 3), (int) (radius * 3), 0, 360);
		}
		
		if (demand.drop.time.get() == model.state.get().time) {
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 10 }, 0));
			graphics.drawArc((int) (dropoffCenterX - radius * 1.5), (int) (dropoffCenterY - radius * 1.5), (int) (radius * 3), (int) (radius * 3), 0, 360);
		}
		
		if (demand.state.get().vehicle == null) {
			graphics.setColor(demand.drop.time.get() < model.state.get().time ? DEMAND_OVERDUE_COLOR : DEMAND_UNDERDUE_COLOR);
			graphics.fillArc((int) (pickupCenterX - radius), (int) (pickupCenterY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		}
		
		graphics.setColor(demand.drop.time.get() < model.state.get().time ? DEMAND_OVERDUE_COLOR : DEMAND_UNDERDUE_COLOR);
		graphics.fillArc((int) (dropoffCenterX - radius), (int) (dropoffCenterY - radius), (int) (radius * 2), (int) (radius * 2), 0, 360);
		
		graphics.setFont(new Font(FONT_NAME, FONT_STYLE, 10));
		
		if (demand.state.get().vehicle == null) {
			String pickupText = Math.round(demand.pick.time.get() / 1000) + "s";
			
			int pickupTextWidth = graphics.getFontMetrics().stringWidth(pickupText);
			int pickupTextHeight = graphics.getFontMetrics().getAscent();
			
			graphics.setColor(Color.BLACK);
			graphics.drawString(pickupText, (int) (pickupCenterX - pickupTextWidth / 2.), (int) (pickupCenterY - pickupTextHeight));
		}
		
		String dropoffText = Math.round(demand.drop.time.get() / 1000) + "s";
		
		int dropoffTextWidth = graphics.getFontMetrics().stringWidth(dropoffText);
		int dropoffTextHeight = graphics.getFontMetrics().getAscent();
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawString(dropoffText, (int) (dropoffCenterX - dropoffTextWidth / 2.), (int) (dropoffCenterY - dropoffTextHeight));
		
	}
	
	private double calculateLength(Segment segment) {
		double startX = calculateX(segment.start.coordinate);
		double startY = calculateY(segment.start.coordinate);
		
		double endX = calculateX(segment.end.coordinate);
		double endY = calculateY(segment.end.coordinate);
		
		double deltaX = endX - startX;
		double deltaY = endY - startY;
		
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
	
	private double calculateDeltaX(Segment segment) {
		double start = calculateX(segment.start.coordinate);		
		double end = calculateX(segment.end.coordinate);
		
		double delta = end - start;
		double length = calculateLength(segment);
		
		return delta / length;
	}
	
	private double calculateDeltaY(Segment segment) {
		double start = calculateY(segment.start.coordinate);		
		double end = calculateY(segment.end.coordinate);
		
		double delta = end - start;
		double length = calculateLength(segment);
		
		return delta / length;
	}
	
	private double calculateCenterX(Vehicle vehicle, Location location) {
		double deltaX = calculateDeltaX(vehicle.state.get().segment);
		double deltaY = calculateDeltaY(vehicle.state.get().segment);
		
		double step = (Math.cos(Math.PI / 2) * deltaX - Math.sin(Math.PI / 2) * deltaY);
		double left = step * vehicle.state.get().segment.lanes.get() * LANE_WIDTH * ratioScreenModel / 2;
		
		Vector coordinate = location.coordinate;
		
		return (int) (calculateX(coordinate) - left + step * (vehicle.state.get().lane + 0.5) * LANE_WIDTH * ratioScreenModel);
	}
	
	private double calculateCenterY(Vehicle vehicle, Location location) {
		double deltaX = calculateDeltaX(location.segment.get());
		double deltaY = calculateDeltaY(location.segment.get());
		
		double step = (Math.sin(Math.PI / 2) * deltaX + Math.cos(Math.PI / 2) * deltaY);
		double left = step * location.segment.get().lanes.get() * LANE_WIDTH * ratioScreenModel / 2;
		
		Vector coordinate = location.coordinate;
		
		return (int) (calculateY(coordinate) - left + step * (vehicle.state.get().lane + 0.5) * LANE_WIDTH * ratioScreenModel);
	}
	
	private double calculateX(Vector coordinate) {
		return (coordinate.x.get() - minLatitude) / rangeLatitude * calculateWidth() + paddingLeft + MARGIN;
	}
	
	private double calculateY(Vector coordinate) {
		return (coordinate.y.get() - minLongitude) / rangeLongitude * calculateHeight() + paddingTop + MARGIN;
	}
	
	private double calculateWidth() {
		return panel.getWidth() - paddingLeft * 2 - MARGIN * 2;
	}
	
	private double calculateHeight() {
		return panel.getHeight() - paddingTop * 2 - MARGIN * 2;
	}

}
