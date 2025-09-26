import argparse
from PIL import Image, ImageFilter
import os

def blur_image(input_path, output_path, blur_level=5):
    """
    Difumina una imagen y la guarda en la ruta especificada
    """
    try:
        # Abrir la imagen
        original_image = Image.open(input_path)
        
        # Aplicar difuminado
        blurred_image = original_image.filter(ImageFilter.GaussianBlur(radius=blur_level))
        
        # Guardar la imagen
        blurred_image.save(output_path)
        print(f"✅ Imagen difuminada guardada en: {output_path}")
        print(f"📊 Nivel de difuminado: {blur_level}")
        
    except Exception as e:
        print(f"❌ Error: {str(e)}")

def main():
    parser = argparse.ArgumentParser(description='Difuminador de imágenes')
    parser.add_argument('input', help='Ruta de la imagen de entrada')
    parser.add_argument('output', help='Ruta donde guardar la imagen difuminada')
    parser.add_argument('--blur', type=int, default=5, 
                       help='Nivel de difuminado (1-20, por defecto: 5)')
    
    args = parser.parse_args()
    
    # Validar nivel de difuminado
    if args.blur < 1 or args.blur > 20:
        print("❌ El nivel de difuminado debe estar entre 1 y 20")
        return
    
    # Validar que el archivo de entrada existe
    if not os.path.exists(args.input):
        print(f"❌ El archivo {args.input} no existe")
        return
    
    # Procesar la imagen
    blur_image(args.input, args.output, args.blur)

if __name__ == "__main__":
    main()
