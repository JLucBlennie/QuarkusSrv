import React, { useState } from 'react';
import Image from 'next/image'
import './HexagonalButton.css'; // Importez le fichier CSS pour le style
import { StaticImageData } from 'next/image';

interface HexagonalButtonProps {
    imageSrc: StaticImageData;
    onClick: () => void;
    disabled: boolean;
}

export default function HexagonalButton(props: HexagonalButtonProps) {
    const [isHovered, setIsHovered] = useState(false);
    const [isClicked, setIsClicked] = useState(false);
    const [isDisabled, setIsDisabled] = useState(props.disabled);

    const handleMouseEnter = () => {
        setIsHovered(true);
    };

    const handleMouseLeave = () => {
        setIsHovered(false);
        setIsClicked(false); // Réinitialiser l'état au sortir de la souris
    };

    const handleMouseDown = () => {
        setIsClicked(true);
    };

    const handleMouseUp = () => {
        setIsClicked(false);
    };

    return (
        <div
            className={`hexagon-button ${isDisabled ? 'disabled': ''} : ${isHovered ? 'glow' : ''} ${isClicked ? 'pressed' : ''}`}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
            onMouseDown={handleMouseDown}
            onMouseUp={handleMouseUp}
            onClick={props.onClick}
        >
            <Image src={props.imageSrc} width={128} height={128} alt="Wireframe" className="image-center" />
        </div>
    );
};