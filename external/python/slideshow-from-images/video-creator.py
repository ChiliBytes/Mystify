#!/usr/bin/env python3
"""
Script to create a compressed MP4 video from images in a folder.
Parameters:
1. Input folder containing images
2. Output folder
3. Seconds between each image transition
"""

import os
import sys
import argparse
from pathlib import Path
import cv2
import numpy as np

def get_image_files(folder_path):
    """Get all image files from the folder, sorted by name."""
    image_extensions = {'.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif', '.webp'}
    image_files = []
    
    for file in sorted(os.listdir(folder_path)):
        file_path = Path(folder_path) / file
        if file_path.suffix.lower() in image_extensions and file_path.is_file():
            image_files.append(str(file_path))
    
    return image_files

def create_video_from_images(input_folder, output_folder,video_name ,transition_seconds, fps=30):
    """Create MP4 video from images with specified transition time."""
    
    # Get all image files
    image_files = get_image_files(input_folder)
    
    if not image_files:
        print(f"No image files found in {input_folder}")
        return False
    
    print(f"Found {len(image_files)} image files")
    
    # Create output directory if it doesn't exist
    os.makedirs(output_folder, exist_ok=True)
    
    # Generate output filename
    output_path = Path(output_folder) / video_name
    
    # Read first image to get dimensions
    first_image = cv2.imread(image_files[0])
    if first_image is None:
        print(f"Could not read first image: {image_files[0]}")
        return False
    
    height, width, _ = first_image.shape
    
    # Calculate number of frames for each image (including transition)
    frames_per_image = int(fps * transition_seconds)
    
    # Define video codec and create VideoWriter
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')  # MP4 codec
    video_writer = cv2.VideoWriter(
        str(output_path), 
        fourcc, 
        fps, 
        (width, height)
    )
    
    try:
        for i, image_path in enumerate(image_files):
            print(f"Processing image {i+1}/{len(image_files)}: {Path(image_path).name}")
            
            # Read current image
            current_image = cv2.imread(image_path)
            if current_image is None:
                print(f"Warning: Could not read image {image_path}, skipping...")
                continue
            
            # Resize image if dimensions don't match
            if current_image.shape[:2] != (height, width):
                current_image = resize_with_padding(current_image, (width, height))

            
            # Write the image for the specified duration
            for _ in range(frames_per_image):
                video_writer.write(current_image)
    
    except Exception as e:
        print(f"Error occurred: {e}")
        return False
    
    finally:
        # Release the video writer
        video_writer.release()
    
    print(f"Video successfully created: {output_path}")
    return True

def resize_with_padding(image, target_size):
    """Resize image maintaining aspect ratio, padding with black borders."""
    target_w, target_h = target_size
    h, w = image.shape[:2]

    # Scale keeping the aspect
    scale = min(target_w / w, target_h / h)
    new_w, new_h = int(w * scale), int(h * scale)
    resized = cv2.resize(image, (new_w, new_h), interpolation=cv2.INTER_AREA)

    # Create black canvas
    canvas = np.zeros((target_h, target_w, 3), dtype=np.uint8)

    # Center scaled image
    x_offset = (target_w - new_w) // 2
    y_offset = (target_h - new_h) // 2
    canvas[y_offset:y_offset+new_h, x_offset:x_offset+new_w] = resized

    return canvas

def main():
    parser = argparse.ArgumentParser(description='Create MP4 video from images in a folder')
    parser.add_argument('input_folder', help='Folder containing input images')
    parser.add_argument('output_folder', help='Output folder for the video')
    parser.add_argument('video_name', help='Name of the resulting video')
    parser.add_argument('transition_seconds', type=float, 
                       help='Seconds between each image transition')
    
    args = parser.parse_args()
    
    # Validate input folder
    if not os.path.isdir(args.input_folder):
        print(f"Error: Input folder '{args.input_folder}' does not exist or is not a directory")
        sys.exit(1)
    
    # Validate transition seconds
    if args.transition_seconds <= 0:
        print("Error: Transition seconds must be a positive number")
        sys.exit(1)
    
    # Create video
    success = create_video_from_images(
        args.input_folder, 
        args.output_folder,
        args.video_name,
        args.transition_seconds
    )
    
    if not success:
        print("Failed to create video")
        sys.exit(1)

if __name__ == "__main__":
    main()
