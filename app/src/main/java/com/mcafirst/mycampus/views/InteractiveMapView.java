package com.mcafirst.mycampus.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.models.Edge;
import com.mcafirst.mycampus.models.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractiveMapView extends View {

    private List<Node> nodes = new ArrayList<>();
    private List<Node> activeRoute = new ArrayList<>();
    private Node currentLocationNode;
    private boolean heatmapEnabled = false;

    private Paint nodePaint;
    private Paint nodeOutlinePaint;
    private Paint edgePaint;
    private Paint routePaint;
    private Paint arrowPaint;
    private Paint textPaint;
    private Paint bgPaint;
    private Paint gridPaint;
    private Paint locationPaint;
    private Paint locationPulsePaint;

    private OnNodeClickListener onNodeClickListener;

    // Icons
    private Map<String, Drawable> typeIcons = new HashMap<>();

    // Zoom and Pan
    private float scaleFactor = 1.0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private ScaleGestureDetector scaleGestureDetector;
    private float lastTouchX;
    private float lastTouchY;

    // Animation for pulse
    private float pulseRadius = 0f;
    private boolean growing = true;

    public interface OnNodeClickListener {
        void onNodeClick(Node node);
    }

    public InteractiveMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#F5F5F5")); // Light Grey Background

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setColor(Color.parseColor("#3F51B5")); // Indigo
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setShadowLayer(8, 0, 4, Color.argb(80, 0, 0, 0));

        nodeOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodeOutlinePaint.setColor(Color.WHITE);
        nodeOutlinePaint.setStyle(Paint.Style.STROKE);
        nodeOutlinePaint.setStrokeWidth(4f);

        edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setColor(Color.parseColor("#BDBDBD"));
        edgePaint.setStrokeWidth(6f);
        edgePaint.setStrokeCap(Paint.Cap.ROUND);

        routePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        routePaint.setColor(Color.parseColor("#2196F3")); // Blue
        routePaint.setStrokeWidth(14f);
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeJoin(Paint.Join.ROUND);
        routePaint.setStrokeCap(Paint.Cap.ROUND);
        routePaint.setPathEffect(new DashPathEffect(new float[]{30, 20}, 0)); // Dotted path

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStrokeWidth(6f);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        arrowPaint.setStrokeJoin(Paint.Join.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#212121"));
        textPaint.setTextSize(32f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        locationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        locationPaint.setColor(Color.parseColor("#2196F3")); // My Location Blue
        locationPaint.setStyle(Paint.Style.FILL);
        locationPaint.setShadowLayer(12, 0, 0, Color.parseColor("#2196F3"));

        locationPulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        locationPulsePaint.setColor(Color.argb(80, 33, 150, 243));
        locationPulsePaint.setStyle(Paint.Style.FILL);

        // Load Icons
        typeIcons.put("Entrance", AppCompatResources.getDrawable(context, R.drawable.ic_gate));
        typeIcons.put("Junction", AppCompatResources.getDrawable(context, R.drawable.ic_junction));
        typeIcons.put("Office", AppCompatResources.getDrawable(context, R.drawable.ic_office));
        typeIcons.put("Academic/Hostel", AppCompatResources.getDrawable(context, R.drawable.ic_academic));
        typeIcons.put("Academic", AppCompatResources.getDrawable(context, R.drawable.ic_academic));
        typeIcons.put("Facility", AppCompatResources.getDrawable(context, R.drawable.ic_facility));
        typeIcons.put("Hostel", AppCompatResources.getDrawable(context, R.drawable.ic_hostel));
        typeIcons.put("Residential", AppCompatResources.getDrawable(context, R.drawable.ic_residential));

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        
        setLayerType(LAYER_TYPE_SOFTWARE, null); // For shadows
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
        invalidate();
    }

    public void setActiveRoute(List<Node> route) {
        this.activeRoute = route;
        invalidate();
    }

    public void setCurrentLocation(Node node) {
        this.currentLocationNode = node;
        invalidate();
    }

    public void setHeatmapEnabled(boolean enabled) {
        this.heatmapEnabled = enabled;
        invalidate();
    }

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.onNodeClickListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

        canvas.save();
        canvas.translate(translateX, translateY);
        canvas.scale(scaleFactor, scaleFactor);

        // Draw grid
        for (int i = 0; i < 3500; i += 100) {
            canvas.drawLine(i, 0, i, 3500, gridPaint);
            canvas.drawLine(0, i, 3500, i, gridPaint);
        }

        if (nodes == null) {
            canvas.restore();
            return;
        }

        // Draw Heatmap
        if (heatmapEnabled) {
            for (Node node : nodes) {
                drawHeatmap(canvas, node);
            }
        }

        // Draw Edges
        for (Node node : nodes) {
            if (node.edges != null) {
                for (Edge edge : node.edges) {
                    canvas.drawLine(node.x, node.y, edge.target.x, edge.target.y, edgePaint);
                }
            }
        }

        // Draw Active Route
        if (activeRoute != null && activeRoute.size() > 1) {
            Path path = new Path();
            path.moveTo(activeRoute.get(0).x, activeRoute.get(0).y);
            for (int i = 1; i < activeRoute.size(); i++) {
                path.lineTo(activeRoute.get(i).x, activeRoute.get(i).y);
            }
            canvas.drawPath(path, routePaint);

            // Draw Arrows along the path
            for (int i = 0; i < activeRoute.size() - 1; i++) {
                drawArrowOnSegment(canvas, activeRoute.get(i), activeRoute.get(i + 1));
            }
        }

        // Draw Nodes
        for (Node node : nodes) {
            // Node circle - Increased size
            float nodeRadius = 65f;
            canvas.drawCircle(node.x, node.y, nodeRadius, nodePaint);
            canvas.drawCircle(node.x, node.y, nodeRadius, nodeOutlinePaint);

            // Draw Icon - Increased size
            Drawable icon = typeIcons.get(node.type);
            if (icon != null) {
                int iconSize = 80;
                icon.setBounds((int)node.x - iconSize/2, (int)node.y - iconSize/2, (int)node.x + iconSize/2, (int)node.y + iconSize/2);
                icon.draw(canvas);
            }
            
            // Text Label with background - Adjusted position for larger circles
            float textWidth = textPaint.measureText(node.name);
            RectF rect = new RectF(node.x - textWidth/2 - 12, node.y + 85, node.x + textWidth/2 + 12, node.y + 130);
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.argb(220, 255, 255, 255));
            canvas.drawRoundRect(rect, 12, 12, rectPaint);
            
            canvas.drawText(node.name, node.x, node.y + 120f, textPaint);
        }

        // Draw Current Location Marker
        if (currentLocationNode != null) {
            updatePulse();
            canvas.drawCircle(currentLocationNode.x, currentLocationNode.y, 40 + pulseRadius, locationPulsePaint);
            canvas.drawCircle(currentLocationNode.x, currentLocationNode.y, 30f, locationPaint);
            canvas.drawCircle(currentLocationNode.x, currentLocationNode.y, 30f, nodeOutlinePaint);
            invalidate(); // Keep animating pulse
        }

        canvas.restore();
    }

    private void drawArrowOnSegment(Canvas canvas, Node start, Node end) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length < 80) return; // Skip very short segments

        // Calculate center of segment for arrow position
        float centerX = (start.x + end.x) / 2;
        float centerY = (start.y + end.y) / 2;

        float angle = (float) Math.atan2(dy, dx);
        float arrowSize = 25f;

        Path arrowPath = new Path();
        // Points towards the 'end' node
        arrowPath.moveTo(centerX, centerY);
        arrowPath.lineTo(
            centerX - arrowSize * (float) Math.cos(angle - Math.PI / 6),
            centerY - arrowSize * (float) Math.sin(angle - Math.PI / 6)
        );
        arrowPath.moveTo(centerX, centerY);
        arrowPath.lineTo(
            centerX - arrowSize * (float) Math.cos(angle + Math.PI / 6),
            centerY - arrowSize * (float) Math.sin(angle + Math.PI / 6)
        );

        canvas.drawPath(arrowPath, arrowPaint);
    }

    private void updatePulse() {
        if (growing) {
            pulseRadius += 1.5f;
            if (pulseRadius > 40f) growing = false;
        } else {
            pulseRadius -= 1.5f;
            if (pulseRadius < 0f) growing = true;
        }
    }

    private void drawHeatmap(Canvas canvas, Node node) {
        // We draw the effect even if density is 0 to show the "green effect" for empty buildings
        
        int color;
        // Updated Thresholds: Green (0-49%) -> Orange (50-69%) -> Red (70-100%)
        if (node.density >= 70) {
            // High Density: Red effect
            color = Color.argb(180, 244, 67, 54); 
        } else if (node.density >= 50) {
            // Moderate Density: Orange effect (Crossed half occupancy)
            color = Color.argb(160, 255, 152, 0);
        } else {
            // Low Density: Green effect (includes 0 density)
            color = Color.argb(140, 76, 175, 80);
        }

        // Radius grows slightly with density for a more dynamic "heat" effect
        float radius = 130f + (node.density * 0.6f);
        
        RadialGradient gradient = new RadialGradient(node.x, node.y, radius, color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        Paint heatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        heatmapPaint.setShader(gradient);
        canvas.drawCircle(node.x, node.y, radius, heatmapPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (nodes == null) return false;
        
        scaleGestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scaleGestureDetector.isInProgress()) {
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;
                    translateX += dx;
                    translateY += dy;
                    invalidate();
                }
                lastTouchX = x;
                lastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                // Check click
                float worldX = (x - translateX) / scaleFactor;
                float worldY = (y - translateY) / scaleFactor;
                for (Node node : nodes) {
                    double distance = Math.sqrt(Math.pow(node.x - worldX, 2) + Math.pow(node.y - worldY, 2));
                    // Increased hit detection area to match larger icons
                    if (distance < 90) {
                        if (onNodeClickListener != null) {
                            onNodeClickListener.onNodeClick(node);
                        }
                        return true;
                    }
                }
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }
}
